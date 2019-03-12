package uk.co.magictractor.oauth.api;

public interface OAuth2Application extends OAuthApplication {

	// TODO! could just be a Class?
	OAuth2ServiceProvider getServiceProvider();

	String getClientId();

	String getClientSecret();

	// https://brandur.org/oauth-scope
	String getScope();

	// default?
	OAuth2AuthorizeResponseType defaultAuthorizeResponseType();

}
