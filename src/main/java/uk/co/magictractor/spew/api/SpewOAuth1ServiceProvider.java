package uk.co.magictractor.spew.api;

public interface SpewOAuth1ServiceProvider extends SpewServiceProvider {

    String oauth1TemporaryCredentialRequestUri();

    String oauth1ResourceOwnerAuthorizationUri();

    String oauth1TokenRequestUri();

    default void initConnectionConfigurationBuilder(SpewOAuth1ConfigurationBuilder configurationBuilder) {
    }

}
