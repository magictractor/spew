package uk.co.magictractor.spew.api;

public interface SpewOAuth2ServiceProvider extends SpewServiceProvider {

    String oauth2AuthorizationUri();

    String oauth2TokenUri();

    // TODO! bin this, configuration builder should handle it
    default void modifyAuthorizationRequest(OutgoingHttpRequest request) {
        // Do nothing.
    }

    // TODO! bin this, configuration builder should handle it
    default void modifyTokenRequest(OutgoingHttpRequest request) {
        // Do nothing.
    }

    default void initConnectionConfigurationBuilder(SpewOAuth2ConfigurationBuilder configurationBuilder) {
    }

}
