package uk.co.magictractor.spew.api;

import java.util.function.BiFunction;

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;

public interface SpewApplication {

    SpewServiceProvider getServiceProvider();

    default SpewConnection getConnection() {
        return SpewConnectionFactory.getConnection(getClass());
    }

    default SpewRequest createRequest(String httpMethod, String url) {
        SpewRequest request = new SpewRequest(this, httpMethod, url);
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

    default AuthorizationHandler getAuthorizationHandler(BiFunction<String, String, Boolean> verificationFunction) {
        return getServiceProvider().getDefaultAuthorizationHandler(verificationFunction);
    }

}
