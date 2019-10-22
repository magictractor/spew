package uk.co.magictractor.spew.oauth.boa;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Random;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth1Configuration;
import uk.co.magictractor.spew.api.connection.AbstractAuthorizationDecoratorConnection;
import uk.co.magictractor.spew.api.connection.SpewConnectionVerificationPendingCache;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.response.parser.text.KeyValuePairsHttpMessageBodyReader;
import uk.co.magictractor.spew.core.verification.AuthVerificationHandler;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.store.EditableProperty;
import uk.co.magictractor.spew.util.BrowserUtil;

// TODO! common interface for OAuth1 and OAuth2 connections (and no auth? / other auth?)
public final class BoaOAuth1Connection extends AbstractAuthorizationDecoratorConnection<SpewOAuth1Configuration> {

    // unit tests can call setSeed() on this
    private final Random nonceGenerator = new Random();

    /**
     * These are generally the persisted properties from
     * SpewOAuth1Configuration, but are replaced with transient unpersisted
     * properties during verification.
     */
    private final EditableProperty userToken;
    private final EditableProperty userSecret;

    @Deprecated(forRemoval = true)
    private final SpewOAuth1Application<?> application;

    /**
     * Default visibility, applications should obtain instances via
     * {@link BoaConnectionFactory#createConnection}, usually indirectly via
     * OAuthConnectionFactory.
     */
    /* default */ BoaOAuth1Connection(SpewOAuth1Application<?> application, SpewOAuth1Configuration configuration) {
        super(configuration);
        this.application = application;
        this.userToken = configuration.getUserTokenProperty();
        this.userSecret = configuration.getUserSecretProperty();
    }

    @Override
    protected boolean hasExistingAuthorization() {
        return userToken.getValue() != null;
        // TODO! check expiry
    }

    @Override
    protected Instant authorizationExpiry() {
        // Yet to find OAuth1 access tokens which expire
        return null;
    }

    @Override
    protected boolean refreshAuthorization() {
        // Should never be called because authorizationExpiry() returns null.
        throw new UnsupportedOperationException("OAuth1 access tokens do not expire");
    }

    @Override
    protected void addAuthorization(OutgoingHttpRequest apiRequest) {
        forAll(apiRequest);
        apiRequest.setQueryStringParam("api_key", getConfiguration().getConsumerKey());
        apiRequest.setQueryStringParam("oauth_token", userToken.getValue());
        addOAuthSignature(apiRequest);
    }

    @Override
    public void obtainAuthorization() {
        AuthVerificationHandler authorizationHandler = getConfiguration().createAuthVerificationHandler();
        authorizationHandler.preOpenAuthorizationInBrowser();

        openAuthorizationUriInBrowser(authorizationHandler.getRedirectUri());

        authorizationHandler.postOpenAuthorizationInBrowser();
    }

    // TODO! split this into two methods
    private void openAuthorizationUriInBrowser(String callback) {
        // TODO! POST?
        OutgoingHttpRequest request = new OutgoingHttpRequest("GET",
            getConfiguration().getTemporaryCredentialRequestUri());

        request.setQueryStringParam("oauth_callback", callback);

        SpewParsedResponse response = authRequest(request);

        String authToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");

        if (authToken == null || authSecret == null) {
            throw new IllegalStateException("missing oauth_token and/or oauth_token_secret values in " + response);
        }

        /**
         * <p>
         * The auth token and secret used for the validation step are used once
         * only until verification is complete. They are reset to persisted
         * properties when a new user token and secret is obtained from
         * successful verification.
         * </p>
         * <p>
         * Alternatively, could persist these values with an immediate expiry???
         * <p>
         */
        //userToken = new ConstantProperty(authToken);
        //userSecret = new ConstantProperty(authSecret);
        userToken.setValue(authToken);
        userSecret.setValue(authSecret);

        // TODO! use OutgoingHttpRequest to build the Uri
        String authUriBase = getConfiguration().getResourceOwnerAuthorizationUri();
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

        SpewConnectionVerificationPendingCache.addVerificationPending(
            req -> authToken.equals(req.getQueryStringParam("oauth_token").orElse(null)), this);

        BrowserUtil.openBrowserTab(authUri);
    }

    private SpewParsedResponse authRequest(OutgoingHttpRequest request) {
        forAll(request);
        addOAuthSignature(request);
        SpewHttpResponse response = request(request);

        return new SpewParsedResponseBuilder(SpewConnectionConfiguration.AUTH, response)
                .withBodyReader(KeyValuePairsHttpMessageBodyReader.class)
                .withoutVerification()
                .build();
    }

    private void addOAuthSignature(OutgoingHttpRequest request) {
        request.setQueryStringParam("oauth_signature", getSignature(request));
    }

    @Override
    public boolean verifyAuthorization(SpewHttpRequest request) {
        String authToken = request.getQueryStringParam("oauth_token").get();
        String verificationCode = request.getQueryStringParam("oauth_verifier").get();

        return verifyAuthorization(authToken, verificationCode);
    }

    @Override
    public boolean verifyAuthorization(String verificationCode) {
        return verifyAuthorization(userToken.getValue(), verificationCode);
    }

    private boolean verifyAuthorization(String authToken, String verificationCode) {
        // TODO! POST? - imagebam allows get or post
        OutgoingHttpRequest request = new OutgoingHttpRequest("GET", getConfiguration().getTokenRequestUri());
        request.setQueryStringParam("oauth_token", authToken);
        request.setQueryStringParam("oauth_verifier", verificationCode);

        SpewParsedResponse response = authRequest(request);

        // Example failure (Flickr)
        // status=401, body=oauth_problem=verifier_invalid
        boolean verified = response.getStatus() == 200;
        if (!verified) {
            return false;
        }

        // Reinstate the persisted properties.
        //userToken = getConfiguration().getUserTokenProperty();
        //userSecret = getConfiguration().getUserSecretProperty();

        String newAuthToken = response.getString("oauth_token");
        String authSecret = response.getString("oauth_token_secret");
        userToken.setValue(newAuthToken);
        userSecret.setValue(authSecret);

        return true;
    }

    private String getSignature(OutgoingHttpRequest request) {
        SpewOAuth1Configuration configuration = getConfiguration();

        String key = configuration.getConsumerSecret() + "&" + userSecret.getValue("");
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        String message = configuration.getSignatureBaseStringFunction().apply(request);
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        byte[] signature = configuration.getSignatureFunction().apply(keyBytes, messageBytes);

        return configuration.getSignatureEncodingFunction().apply(signature);
    }

    private void forAll(OutgoingHttpRequest request) {
        SpewOAuth1Configuration configuration = getConfiguration();

        // hmm... same as api_key? (in forApi())
        request.setQueryStringParam("oauth_consumer_key", configuration.getConsumerKey());

        // TODO! nonce should guarantee that it is never the same if the
        // timestamp has not move on since the last API call. Not quite guaranteed here
        // - but pretty darned likely.
        // https://oauth.net/core/1.0a/#nonce
        request.setQueryStringParam("oauth_nonce", nonceGenerator.nextInt());
        request.setQueryStringParam("oauth_timestamp", System.currentTimeMillis() / 1000);

        request.setQueryStringParam("oauth_version", "1.0");
        request.setQueryStringParam("oauth_signature_method", configuration.getRequestSignatureMethod());

        super.getProperties();
    }

}
