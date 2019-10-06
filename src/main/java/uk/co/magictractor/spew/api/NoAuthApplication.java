package uk.co.magictractor.spew.api;

/**
 * An application with no authentication or authorization.
 */
@SpewAuthType("None")
public interface NoAuthApplication<SP extends SpewServiceProvider> extends SpewApplication<SP> {

}
