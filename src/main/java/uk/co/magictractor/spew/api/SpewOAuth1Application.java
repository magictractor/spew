package uk.co.magictractor.spew.api;

// OAuth1 specification https://tools.ietf.org/html/rfc5849
@SpewAuthType("OAuth1")
public interface SpewOAuth1Application<SP extends SpewOAuth1ServiceProvider>
        extends SpewApplication<SP>, /* SpewOAuth1Configuration, */ HasCallbackServer {

    default SpewOAuth1Configuration getConfiguration() {
        return new SpewOAuth1ConfigurationBuilder().withApplication(this).build();
    }

    // @Override
    default String getTemporaryCredentialRequestUri() {
        return getServiceProvider().getTemporaryCredentialRequestUri();
    }

    //@Override
    default String getResourceOwnerAuthorizationUri() {
        return getServiceProvider().getResourceOwnerAuthorizationUri();
    }

    //@Override
    default String getTokenRequestUri() {
        return getServiceProvider().getTokenRequestUri();
    }

    //@Override
    default String getRequestSignatureMethod() {
        return getServiceProvider().getRequestSignatureMethod();
    }

    //@Override
    default String getJavaSignatureMethod() {
        return getServiceProvider().getJavaSignatureMethod();
    }

    @Override
    default String getOutOfBandUri() {
        return "oob";
    }

}
