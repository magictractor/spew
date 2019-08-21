package uk.co.magictractor.spew.oauth.boa;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.text.KeyValuePairsResponse;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.core.verification.VerificationInfo;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.store.UserPropertyStore;
import uk.co.magictractor.spew.util.BrowserUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

// TODO! common interface for OAuth1 and OAuth2 connections (and no auth? / other auth?)
public final class BoaOAuth1Connection<SP extends SpewOAuth1ServiceProvider>
        extends AbstractBoaOAuthConnection<SpewOAuth1Application<SP>, SP> {

    // unit tests can call setSeed() on this
    private final Random nonceGenerator = new Random();

    /**
     * Temporary tokens and secrets are held in memory and not persisted.
     */
    private final EditableProperty userToken;
    private final EditableProperty userSecret;

    /**
     * Default visibility, applications should obtain instances via
     * {@link BoaConnectionFactory#createConnection}, usually indirectly via
     * OAuthConnectionFactory.
     */
    /* default */ BoaOAuth1Connection(SpewOAuth1Application<SP> application) {
        super(application);

        this.userToken = SPIUtil.firstAvailable(UserPropertyStore.class).getProperty(application, "user_token");
        this.userSecret = SPIUtil.firstAvailable(UserPropertyStore.class).getProperty(application, "user_secret");
    }

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest apiRequest) {
        // TODO! (optionally?) verify existing tokens? - and need similar for spring social
        if (userToken.getValue() == null) {
            authorizeUser();
        }

        forAll(apiRequest);
        forApi(apiRequest);
        return ExceptionUtil.call(() -> request0(apiRequest, this::addOAuthSignature));
    }

    private void addOAuthSignature(OutgoingHttpRequest request) {
        request.setQueryStringParam("oauth_signature", getSignature(request));
    }

    private SpewParsedResponse authRequest(OutgoingHttpRequest apiRequest) {
        forAll(apiRequest);
        SpewHttpResponse response = ExceptionUtil.call(() -> request0(apiRequest, this::addOAuthSignature));

        return new KeyValuePairsResponse(response);
    }

    private void authorizeUser() {
        AuthorizationHandler authorizationHandler = getApplication()
                .getAuthorizationHandler(BoaOAuth1VerificationFunction::new);
        authorizationHandler.preOpenAuthorizationInBrowser(getApplication());

        openAuthorizationUriInBrowser(authorizationHandler.getCallbackValue());

        authorizationHandler.postOpenAuthorizationInBrowser(getApplication());
    }

    private void openAuthorizationUriInBrowser(String callback) {
        // TODO! POST?
        OutgoingHttpRequest request = getApplication()
                .createGetRequest(getServiceProvider().getTemporaryCredentialRequestUri());

        request.setQueryStringParam("oauth_callback", callback);

        SpewParsedResponse response = authRequest(request);

        String authToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");

        if (authToken == null || authSecret == null) {
            throw new IllegalStateException("missing oauth_token and/or oauth_token_secret values in " + response);
        }

        // These are temporary values, only used to get the user's token and secret, so
        // don't persist them.
        userToken.setUnpersistedValue(authToken);
        userSecret.setUnpersistedValue(authSecret);

        String authUriBase = getServiceProvider().getResourceOwnerAuthorizationUri();
        StringBuilder authUriBuilder = new StringBuilder();
        authUriBuilder.append(authUriBase);
        if (authUriBase.contains("?")) {
            // Already has query string, perhaps for permission types, like Flickr
            authUriBuilder.append('&');
        }
        else {
            authUriBuilder.append('?');
        }
        authUriBuilder.append("oauth_token=");
        authUriBuilder.append(authToken);
        String authUri = authUriBuilder.toString();

        BrowserUtil.openBrowserTab(authUri);
    }

    private boolean verifyAuthentication(VerificationInfo verificationInfo) {
        String verificationAuthToken = verificationInfo.getAuthToken();
        if (verificationAuthToken == null) {
            // Could assert for oob here? Should only happen when pasting values.
            verificationAuthToken = userToken.getValue();
        }

        try {
            fetchToken(verificationAuthToken, verificationInfo.getVerificationCode());
            return true;
        }
        catch (Exception e) {
            System.err.println(e);
            return false;
        }
    }

    private void fetchToken(String authToken, String verificationCode) {
        // TODO! POST? - imagebam allows get or post
        OutgoingHttpRequest request = getApplication().createGetRequest(getServiceProvider().getTokenRequestUri());
        request.setQueryStringParam("oauth_token", authToken);
        request.setQueryStringParam("oauth_verifier", verificationCode);
        SpewParsedResponse response = authRequest(request);

        String newAuthToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");
        userToken.setValue(newAuthToken);
        userSecret.setValue(authSecret);
    }

    // See https://www.flickr.com/services/api/auth.oauth.html
    // https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
    private String getSignature(OutgoingHttpRequest request) {
        // AARGH - ImageBam refers to "key" where I have used
        // getSignatureBaseString(request)
        // TODO! consumer key only absent for auth
        // TODO! different service providers have different strategies for the key?!
        // Flickr:
        String key = getApplication().getConsumerSecret() + "&" + userSecret.getValue("");
        // ImageBam
        // oauth_signature = MD5(API-key + API-secret + oauth_timestamp + oauth_nonce +
        // oauth_token + oauth_token_secret)
        //		String key = application.getAppToken() + application.getAppSecret() + request.getParam("oauth_timestamp")
        //				+ request.getParam("oauth_nonce") + request.getParam("oauth_token")
        //				+ request.getParam("oauth_token_secret");
        //		System.err.println("key: " + key);

        // TODO! Java signature name and Api not identical
        String signatureMethod = getServiceProvider().getJavaSignatureMethod();
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), signatureMethod);
        Mac mac = ExceptionUtil.call(() -> Mac.getInstance(signatureMethod));
        ExceptionUtil.call(() -> mac.init(signingKey));

        // String signature =
        // Base64.getEncoder().encodeToString(mac.doFinal(getSignatureBaseString(request).getBytes()));
        String signature;
        if (getServiceProvider() instanceof ImageBam) {
            // PHP example on ImageBam wiki uses MD5() function which returns hex
            // different base string, different hashing, and different encoding
            // ah! it's md5 - not HMAC-md5?!
            // https://www.ietf.org/rfc/rfc2104.txt - HMAC
            // and a different base string!
            byte[] bytes = ExceptionUtil.call(
                () -> MessageDigest.getInstance("MD5").digest(getImageBamSignatureBaseString(request).getBytes()));
            // and a different encoding!
            signature = BaseEncoding.base16().lowerCase().encode(bytes);
        }
        else {
            // Flickr, Twitter
            // TODO! get some more examples of OAuth1 before tidying this mess up??
            // signature =
            // Base64.getEncoder().encodeToString(mac.doFinal(getSignatureBaseString(request).getBytes()));
            signature = BaseEncoding.base64().encode(mac.doFinal(getSignatureBaseString(request).getBytes()));
        }

        getLogger().trace("signature: {}", signature);

        // The value in the query string params map should not be encoded.
        // It will be encoded when the query is built.
        // return ExceptionUtil.call(() -> URLEncoder.encode(signature, "UTF-8"));
        return signature;
    }

    private String getImageBamSignatureBaseString(OutgoingHttpRequest request) {
        StringBuilder signatureBaseStringBuilder = new StringBuilder();

        signatureBaseStringBuilder.append(getApplication().getConsumerKey());
        signatureBaseStringBuilder.append(getApplication().getConsumerSecret());
        signatureBaseStringBuilder.append(request.getQueryStringParam("oauth_timestamp").get());
        signatureBaseStringBuilder.append(request.getQueryStringParam("oauth_nonce").get());
        if (userToken.getValue() != null) {
            signatureBaseStringBuilder.append(userToken.getValue());
            signatureBaseStringBuilder.append(userSecret.getValue());
        }

        return signatureBaseStringBuilder.toString();
    }

    // See https://www.flickr.com/services/api/auth.oauth.html
    private String getSignatureBaseString(OutgoingHttpRequest request) {
        StringBuilder signatureBaseStringBuilder = new StringBuilder();
        signatureBaseStringBuilder.append(request.getHttpMethod());
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(oauthEncode(request.getPath()));
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(oauthEncode(getSignatureQueryString(request)));

        return signatureBaseStringBuilder.toString();
    }

    private String getSignatureQueryString(OutgoingHttpRequest request) {
        // TODO! maybe ignore some params - see Flickr upload photo
        TreeMap<String, Object> orderedParams = new TreeMap<>(request.getQueryStringParams());
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : orderedParams.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                stringBuilder.append('&');
            }
            stringBuilder.append(entry.getKey());
            stringBuilder.append('=');
            stringBuilder.append(oauthEncode(entry.getValue().toString()));
        }

        return stringBuilder.toString();
    }

    // https://stackoverflow.com/questions/5864954/java-and-rfc-3986-uri-encoding
    private final String oauthEncode(String string) {
        // TODO! something more efficient?
        String urlEncoded = ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
        return urlEncoded.replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }

    private void forApi(OutgoingHttpRequest request) {
        request.setQueryStringParam("api_key", getApplication().getConsumerKey());
        request.setQueryStringParam("oauth_token", userToken.getValue());
    }

    private void forAll(OutgoingHttpRequest request) {
        // hmm... same as api_key? (in forApi())
        request.setQueryStringParam("oauth_consumer_key", getApplication().getConsumerKey());

        // TODO! nonce should guarantee that it is never the same if the
        // timestamp has not move on since the last API call. Not quite guaranteed here
        // - but pretty darned likely.
        // https://oauth.net/core/1.0a/#nonce
        request.setQueryStringParam("oauth_nonce", nonceGenerator.nextInt());
        request.setQueryStringParam("oauth_timestamp", System.currentTimeMillis() / 1000);

        request.setQueryStringParam("oauth_version", "1.0");
        request.setQueryStringParam("oauth_signature_method", getServiceProvider().getRequestSignatureMethod());
    }

    private class BoaOAuth1VerificationFunction implements VerificationFunction {

        @Override
        public Boolean apply(VerificationInfo info) {
            return BoaOAuth1Connection.this.verifyAuthentication(info);
        }

        @Override
        public SpewConnection getConnection() {
            return BoaOAuth1Connection.this;
        }
    }

}
