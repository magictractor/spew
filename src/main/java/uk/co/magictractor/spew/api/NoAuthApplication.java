package uk.co.magictractor.spew.api;

/**
 * An application with no authentication or authorization.
 */
@SpewAuthType
public interface NoAuthApplication<SP extends SpewServiceProvider> extends SpewApplication<SP> {

}
