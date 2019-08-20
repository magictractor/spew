package uk.co.magictractor.spew.api;

/**
 * Application code should not need to interact with SpewConnection instances.
 */
public interface SpewConnection {

    /**
     * <p>
     * Unique id which permits web request handlers to fetch a connection from
     * the cache using an id.
     * </p>
     * <p>
     * That permits information about the connection, such as application and
     * service provider names, to be displayed when verification succeeds or
     * fails.
     * </p>
     */
    default String getId() {
        return Integer.toString(System.identityHashCode(this));
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

}
