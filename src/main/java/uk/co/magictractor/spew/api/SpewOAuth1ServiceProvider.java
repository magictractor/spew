package uk.co.magictractor.spew.api;

public interface SpewOAuth1ServiceProvider extends SpewServiceProvider {

    default SpewOAuth1ConfigurationBuilder getConfigurationBuilder() {
        return new SpewOAuth1ConfigurationBuilder()
                .withServiceProvider(this);
    }

    String getTemporaryCredentialRequestUri();

    String getResourceOwnerAuthorizationUri();

    String getTokenRequestUri();

    String getRequestSignatureMethod();

}
