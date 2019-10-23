package uk.co.magictractor.spew.api;

public interface SpewOAuth2ServiceProvider extends SpewServiceProvider {

    String oauth2AuthorizationUri();

    String oauth2TokenUri();

    default void initConnectionConfigurationBuilder(SpewOAuth2ConfigurationBuilder configurationBuilder) {
    }

}
