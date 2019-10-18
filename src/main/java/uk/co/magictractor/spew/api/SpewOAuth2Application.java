package uk.co.magictractor.spew.api;

import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

// OAuth2 specification https://tools.ietf.org/html/rfc6749
@SpewAuthType("OAuth2")
public interface SpewOAuth2Application<SP extends SpewOAuth2ServiceProvider> extends SpewApplication<SP> {

    default SpewOAuth2Configuration getConfiguration() {
        return getServiceProvider()
                .oauth2ConfigurationBuilder()
                .withApplication(this)
                .build();
    }

    default String getClientId() {
        // TODO! firstNonNull would be better
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "client_id");
    }

    default String getClientSecret() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "client_secret");
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
     */
    // https://brandur.org/oauth-scope
    // https://tools.ietf.org/html/rfc6749#section-3.3
    default String getScope() {
        return null;
    }

}
