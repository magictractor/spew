package uk.co.magictractor.spew.api;

import java.util.Collections;
import java.util.Map;

/**
 * Application code should not need to interact with SpewConnection instances.
 */
public interface SpewConnection {

    // TODO! remove this?? (connection layer should not be aware of the application layer)
    SpewApplication<?> getApplication();

    // Typically used to add authorization information to the request.
    // This is no called for requests which fetch authoentication tokens etc.
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

    /**
     * A summary of the state of the connection, displayed after authorization.
     */
    default Map<String, Object> getProperties() {
        return Collections.emptyMap();
    }

}
