package uk.co.magictractor.spew.api;

import java.util.Map;

/**
 * Application code should not need to interact with SpewConnection instances.
 */
public interface SpewConnection extends HasProperties {

    String getId();

    // Typically used to add authorization information to the request.
    // This is not called for requests which fetch authorization tokens etc.
    default void prepareApplicationRequest(OutgoingHttpRequest request) {
    }

    /**
     * <p>
     * This is used to make authorisation requests as well as API requests.
     * </p>
     * <p>
     * Application code should not use this directly, TODO! doc preferred method
     * for application code.
     * </p>
     */
    SpewHttpResponse request(OutgoingHttpRequest apiRequest);

    @Override
    default void addProperties(Map<String, Object> properties) {
        // Do nothing
    }

}
