package uk.co.magictractor.spew.api;

import java.util.function.Supplier;

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.api.connection.SpewConnectionCache;

public interface SpewApplication {

    SpewServiceProvider getServiceProvider();

    default SpewConnection getConnection() {
        return SpewConnectionCache.getConnection(getClass());
    }

    default SpewRequest createRequest(String httpMethod, String url) {
        SpewRequest request = new SpewRequest(this, httpMethod, url);
        // TODO! skip prep during auth
        prepareRequest(request);
        return request;
    }

    default void prepareRequest(SpewRequest request) {
        getServiceProvider().prepareRequest(request);
    }

    default String getContentType(SpewResponse response) {
        return getServiceProvider().getContentType(response);
    }

    default SpewRequest createGetRequest(String url) {
        return createRequest("GET", url);
    }

    default SpewRequest createPostRequest(String url) {
        return createRequest("POST", url);
    }

    default SpewRequest createDelRequest(String url) {
        return createRequest("DEL", url);
    }

    default AuthorizationHandler getAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        return getServiceProvider().getDefaultAuthorizationHandler(verificationFunctionSupplier);
    }

}
