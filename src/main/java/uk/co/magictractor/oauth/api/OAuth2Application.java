package uk.co.magictractor.oauth.api;

public interface OAuth2Application extends OAuthApplication { // <OAuth2ServiceProvider> {

    OAuth2ServiceProvider getServiceProvider();

    String getClientId();

    String getClientSecret();

    // https://brandur.org/oauth-scope
    String getScope();

    // default?
    OAuth2AuthorizeResponseType defaultAuthorizeResponseType();

}
