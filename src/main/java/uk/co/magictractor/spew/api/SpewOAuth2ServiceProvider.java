package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.api.connection.HasConnectionConfigurationBuilder;

public interface SpewOAuth2ServiceProvider
        extends SpewServiceProvider, HasConnectionConfigurationBuilder<SpewOAuth2ConfigurationBuilder> {

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
