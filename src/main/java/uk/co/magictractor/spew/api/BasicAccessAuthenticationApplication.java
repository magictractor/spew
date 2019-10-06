package uk.co.magictractor.spew.api;

/**
 * An application with basic access authentication.
 * https://en.wikipedia.org/wiki/Basic_access_authentication
 */
@SpewAuthType("Basic")
public interface BasicAccessAuthenticationApplication<SP extends SpewServiceProvider> extends SpewApplication<SP> {

}
