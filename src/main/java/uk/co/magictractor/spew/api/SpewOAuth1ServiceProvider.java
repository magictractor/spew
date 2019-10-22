package uk.co.magictractor.spew.api;

public interface SpewOAuth1ServiceProvider extends SpewServiceProvider {

    String oauth1TemporaryCredentialRequestUri();

    String oauth1ResourceOwnerAuthorizationUri();

    String oauth1TokenRequestUri();

    // TODO! config builder only for this
    String oauth1RequestSignatureMethod();

    default void initConnectionConfigurationBuilder(SpewOAuth1ConfigurationBuilder configurationBuilder) {
    }

}
