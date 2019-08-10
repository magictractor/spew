package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.server.OAuth2VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;
import uk.co.magictractor.spew.server.ShutdownOnceVerifiedRequestHandler;
import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

@SpewAuthType
public interface SpewOAuth2Application extends SpewApplication, HasCallbackServer {

    @Override
    SpewOAuth2ServiceProvider getServiceProvider();

    default String getClientId() {
        // TODO! firstNonNull would be better
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "client_id");
    }

    default String getClientSecret() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "client_secret");
    }

    default void modifyAuthorizationRequest(SpewRequest request) {
        getServiceProvider().modifyAuthorizationRequest(request);
    }

    default void modifyTokenRequest(SpewRequest request) {
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

    @Override
    default List<RequestHandler> getServerRequestHandlers(Supplier<VerificationFunction> verificationFunctionSupplier) {
        return Arrays.asList(
            new OAuth2VerificationRequestHandler(verificationFunctionSupplier),
            new ShutdownOnceVerifiedRequestHandler(),
            new ResourceRequestHandler(serverResourcesRelativeToClass()));
    }

}
