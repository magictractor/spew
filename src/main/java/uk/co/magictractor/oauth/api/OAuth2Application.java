package uk.co.magictractor.oauth.api;

public interface OAuth2Application {

	// TODO! could just be a Class?
	OAuth2ServiceProvider getServiceProvider();

	String getClientId();

	// https://brandur.org/oauth-scope
	String getScope();

}
