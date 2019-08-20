package uk.co.magictractor.spew.api;

import java.util.function.Supplier;

import uk.co.magictractor.spew.api.connection.SpewConnectionCache;
import uk.co.magictractor.spew.core.verification.AuthorizationHandler;
import uk.co.magictractor.spew.core.verification.VerificationFunction;

public interface SpewApplication {

    SpewServiceProvider getServiceProvider();

    default SpewConnection getConnection() {
        return SpewConnectionCache.getOrCreateConnection(getClass());
    }

    default OutgoingHttpRequest createRequest(String httpMethod, String url) {
        OutgoingHttpRequest request = new OutgoingHttpRequest(this, httpMethod, url);
        // TODO! skip prep during auth
        prepareRequest(request);
        return request;
    }

    default void prepareRequest(OutgoingHttpRequest request) {
        getServiceProvider().prepareRequest(request);
    }

    default String getContentType(SpewHttpResponse response) {
        return getServiceProvider().getContentType(response);
    }

    default OutgoingHttpRequest createGetRequest(String url) {
        return createRequest("GET", url);
    }

    default OutgoingHttpRequest createPostRequest(String url) {
        return createRequest("POST", url);
    }

    default OutgoingHttpRequest createDelRequest(String url) {
        return createRequest("DEL", url);
    }

    default AuthorizationHandler getAuthorizationHandler(Supplier<VerificationFunction> verificationFunctionSupplier) {
        return getServiceProvider().getDefaultAuthorizationHandler(verificationFunctionSupplier);
    }

}
