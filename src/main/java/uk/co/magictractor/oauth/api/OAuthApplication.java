package uk.co.magictractor.oauth.api;

public interface OAuthApplication { // <SP extends OAuthServiceProvider> {

	// generics for connection too?
	OAuthConnection getConnection();

	// SP getServiceProvider();
	OAuthServiceProvider getServiceProvider();

}
