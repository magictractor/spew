package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.api.connection.HasConnectionConfigurationBuilder;

// OAuth2 specification https://tools.ietf.org/html/rfc6749
@SpewAuthType("OAuth2")
public interface SpewOAuth2Application<SP extends SpewOAuth2ServiceProvider>
        extends SpewApplication<SP>, HasConnectionConfigurationBuilder<SpewOAuth2ConfigurationBuilder> {

    default SpewOAuth2Configuration getConfiguration() {
        return new SpewOAuth2ConfigurationBuilder()
                .withApplication(this)
                .build();
    }

    default void modifyAuthorizationRequest(OutgoingHttpRequest request) {
        getServiceProvider().modifyAuthorizationRequest(request);
    }

    default void modifyTokenRequest(OutgoingHttpRequest request) {
        getServiceProvider().modifyTokenRequest(request);
    }

    /**
     * <p>
     * Scope may be specified for an application to restrict which API calls may
     * be used, for example restricting the application to read only calls and
     * not being able to read any profile information.
     * </p>
     * <p>
     * Values used to restrict the scope are specified and documented by the
     * service provider.
     * </p>
     * <p>
     * Scope is only used by some service providers.
     * </p>
     * <p>
     * This method may be overridden to provide a scope for an application,
     * alternatively withScope() may be used on the configuration builder.
     * </p>
     */
    // https://brandur.org/oauth-scope
    // https://tools.ietf.org/html/rfc6749#section-3.3
    default String getScope() {
        return null;
    }

}
