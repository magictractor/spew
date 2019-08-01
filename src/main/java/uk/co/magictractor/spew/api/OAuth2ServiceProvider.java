package uk.co.magictractor.spew.api;

public interface OAuth2ServiceProvider extends SpewServiceProvider {

    String getAuthorizationUri();

    String getTokenUri();

}
