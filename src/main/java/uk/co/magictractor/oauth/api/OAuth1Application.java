package uk.co.magictractor.oauth.api;

public interface OAuth1Application extends OAuthApplication {

	OAuth1ServiceProvider getServiceProvider();

	String getAppToken();
	
	String getAppSecret();
}
