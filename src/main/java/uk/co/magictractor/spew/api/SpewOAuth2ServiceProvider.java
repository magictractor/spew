package uk.co.magictractor.spew.api;

public interface SpewOAuth2ServiceProvider extends SpewServiceProvider {

    default SpewOAuth2ConfigurationBuilder getConfigurationBuilder() {
        return new SpewOAuth2ConfigurationBuilder()
                .withServiceProvider(this);
    }

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

}
