package uk.co.magictractor.spew.api;

/**
 * Application code should not need to interact with SpewConnection instances.
 */
public interface SpewConnection {

    /**
     * <p>
     * This is used to make authorisation requests as well as API requests.
     * </p>
     * <p>
     * Application code should not use this directly, TODO! doc preferred method
     * for application code.
     * </p>
     */
    SpewResponse request(SpewRequest apiRequest);

}
