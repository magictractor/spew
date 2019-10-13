package uk.co.magictractor.spew.api;

public interface SpewOAuth1ServiceProvider extends SpewServiceProvider {

    default SpewOAuth1ConfigurationBuilder getConfigurationBuilder() {
        return new SpewOAuth1ConfigurationBuilder()
                .withServiceProvider(this);
    }

    String oauth1TemporaryCredentialRequestUri();

    String oauth1ResourceOwnerAuthorizationUri();

    String oauth1TokenRequestUri();

    String oauth1RequestSignatureMethod();

}
