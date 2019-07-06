package uk.co.magictractor.spew.api;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.io.BaseEncoding;

import uk.co.magictractor.spew.imagebam.ImageBam;
import uk.co.magictractor.spew.token.UserPreferencesPersister;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.UrlEncoderUtil;

// TODO! common interface for OAuth1 and OAuth2 connections (and no auth? / other auth?)
public final class OAuth1Connection extends AbstractOAuthConnection<OAuth1Application, OAuth1ServiceProvider> {

    //private final OAuth1Application application;

    // unit tests can call setSeed() on this
    private final Random nonceGenerator = new Random();

    /**
     * Temporary tokens and secrets are held in memory and not persisted.
     */
    private final UserPreferencesPersister userToken;
    private final UserPreferencesPersister userSecret;

    // TODO! change this to default then use MyApp.getInstance().getConnection()
    /**
     * Default visibility, applications should obtain instances via
     * OAuth1Application.getConnection().
     */
    public OAuth1Connection(OAuth1Application application) {
        super(application);
        //this.application = application;

        this.userToken = new UserPreferencesPersister(application, "user_token");
        this.userSecret = new UserPreferencesPersister(application, "user_secret");
    }

    @Override
    public OAuthResponse request(OAuthRequest apiRequest) {
        // TODO! (optionally?) verify existing tokens?
        if (userToken.getValue() == null) {
            authenticateUser();
        }

        forAll(apiRequest);
        forApi(apiRequest);
        return ExceptionUtil.call(() -> request0(apiRequest, getServiceProvider().getJsonConfiguration()));
    }

    private OAuthResponse authRequest(OAuthRequest apiRequest) {
        forAll(apiRequest);
        return ExceptionUtil.call(() -> request0(apiRequest, getServiceProvider().getJsonConfiguration()));
    }

    private void authenticateUser() {
        authorize();

        Scanner scanner = new Scanner(System.in);
        // System.err.println("Enter verification code for oauth_token=" + requestToken
        // + ": ");
        System.err.println("Enter verification code: ");
        String verification = scanner.nextLine().trim();
        // FlickrConfig.setUserAuthVerifier(verification);
        scanner.close();

        fetchToken(verification);
    }

    private void authorize() {
        // TODO! POST?
        OAuthRequest request = OAuthRequest
                .createGetRequest(getServiceProvider().getTemporaryCredentialRequestUri());
        OAuthResponse response = authRequest(request);

        String authToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");

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

        // https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
        if (Desktop.isDesktopSupported()) {
            // uri = new
            ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(authUri)));
        }
        else {
            throw new UnsupportedOperationException("TODO");
        }
    }

    private void fetchToken(String verification) {
        // FlickrRequest request = FlickrRequest.forAuth("access_token");
        // TODO! POST? - imagebam allows get or post
        OAuthRequest request = OAuthRequest.createGetRequest(getServiceProvider().getTokenRequestUri());
        request.setParam("oauth_token", userToken.getValue());
        request.setParam("oauth_verifier", verification);
        OAuthResponse response = authRequest(request);

        String authToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");
        userToken.setValue(authToken);
        userSecret.setValue(authSecret);
    }

    // hmms - push some of this up? - encode could be different per connection?
    @Override
    protected String getUrl(OAuthRequest request) {
        // String unsignedUrl = request.getUrl() + "?" +
        // getQueryString(request.getParams());

        // urlBuilder.append("oauth_signature=");
        // urlBuilder.append(getSignature());

        return request.getUrl() + "?" + getQueryString(request.getParams(), UrlEncoderUtil::paramEncode)
                + "&oauth_signature=" + getSignature(request);
    }

    // See https://www.flickr.com/services/api/auth.oauth.html
    // https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
    private String getSignature(OAuthRequest request) {
        //		if (userSecret == null) {
        //			throw new IllegalArgumentException("userSecret must not be null (it may be an empty string)");
        //		}

        // AARGH - ImageBam refers to "key" where I have used
        // getSignatureBaseString(request)
        // TODO! consumer key only absent for auth
        // TODO! different service providers have different strategies for the key?!
        // Flickr:
        String key = getApplication().getAppSecret() + "&" + userSecret.getValue("");
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
        if (ImageBam.getInstance().equals(getServiceProvider())) {
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
            // Flickr
            // TODO! get some more examples of OAuth1 before tidying this mess up??
            // signature =
            // Base64.getEncoder().encodeToString(mac.doFinal(getSignatureBaseString(request).getBytes()));
            signature = BaseEncoding.base64().encode(mac.doFinal(getSignatureBaseString(request).getBytes()));
        }

        getLogger().trace("signature: " + signature);

        return ExceptionUtil.call(() -> URLEncoder.encode(signature, "UTF-8"));
    }

    private String getImageBamSignatureBaseString(OAuthRequest request) {
        StringBuilder signatureBaseStringBuilder = new StringBuilder();

        signatureBaseStringBuilder.append(getApplication().getAppToken());
        signatureBaseStringBuilder.append(getApplication().getAppSecret());
        signatureBaseStringBuilder.append(request.getParam("oauth_timestamp"));
        signatureBaseStringBuilder.append(request.getParam("oauth_nonce"));
        if (userToken.getValue() != null) {
            signatureBaseStringBuilder.append(userToken.getValue());
            signatureBaseStringBuilder.append(userSecret.getValue());
        }

        return signatureBaseStringBuilder.toString();
    }

    // See https://www.flickr.com/services/api/auth.oauth.html
    private String getSignatureBaseString(OAuthRequest request) {
        StringBuilder signatureBaseStringBuilder = new StringBuilder();
        signatureBaseStringBuilder.append(request.getHttpMethod());
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(UrlEncoderUtil.oauthEncode(request.getUrl()));
        signatureBaseStringBuilder.append('&');
        signatureBaseStringBuilder.append(
            UrlEncoderUtil.oauthEncode(getQueryString(getBaseStringParams(request), UrlEncoderUtil::oauthEncode)));

        return signatureBaseStringBuilder.toString();
    }

    //	private String urlEncode(String string) {
    //		return ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
    //	}

    /** @return ordered params for building signature key */
    private Map<String, Object> getBaseStringParams(OAuthRequest request) {
        // TODO! some params should be ignored
        // TODO! where should params be escaped??
        return new TreeMap<>(request.getParams());
    }

    private void forApi(OAuthRequest request) {
        // OAuthRequest request = new OAuthRequest(FLICKR_REST_ENDPOINT);
        //		setParam("oauth_consumer_key", FlickrConfig.API_KEY);

        request.setParam("api_key", getApplication().getAppToken());
        request.setParam("oauth_token", userToken.getValue());
        // request.setParam("method", flickrMethod);
        request.setParam("format", "json");
        request.setParam("nojsoncallback", "1");

        // return request;
    }

    private void forAll(OAuthRequest request) {
        // hmm... same as api_key? (in forApi())
        request.setParam("oauth_consumer_key", getApplication().getAppToken());

        // TODO! nonce should guarantee that it is never the same if the
        // timestamp has not move on since the last API call. Not quite guaranteed here
        // - but pretty darned likely.
        // https://oauth.net/core/1.0a/#nonce
        request.setParam("oauth_nonce", nonceGenerator.nextInt());
        request.setParam("oauth_timestamp", System.currentTimeMillis() / 1000);
        // setParam("oauth_callback", "www.google.com");
        // "oob" so that web shows the verifier which can then be copied
        // eh? should only need "oob" during authorization
        request.setParam("oauth_callback", "oob");
        request.setParam("oauth_version", "1.0");
        request.setParam("oauth_signature_method", getServiceProvider().getRequestSignatureMethod());
    }

}

//  https://api.flickr.com/services/rest/?method=flickr.photos.setMeta&api_key=5939e168bc6ea2e41e83b74f6f0b3e2d&photo_id=45249983521&title=Small+white&format=json&nojsoncallback=1&auth_token=72157672277056577-9fa9087d61430e0a&api_sig=2e45756e2c8c451dc08468ab15b7c9ac
