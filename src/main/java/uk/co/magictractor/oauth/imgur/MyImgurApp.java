package uk.co.magictractor.oauth.imgur;

import uk.co.magictractor.oauth.api.OAuth2Application;
import uk.co.magictractor.oauth.api.OAuth2AuthorizeResponseType;
import uk.co.magictractor.oauth.api.OAuth2Connection;
import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;
import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.processor.properties.OAuth2CachedResourceFileProperties;

public class MyImgurApp extends OAuth2CachedResourceFileProperties implements OAuth2Application {

	private static final MyImgurApp INSTANCE = new MyImgurApp();

	// TODO! common class for connection cache - see MyGooglePhotosApp
	private OAuth2Connection connection;

	private MyImgurApp() {
	}

	public static MyImgurApp getInstance() {
		return INSTANCE;
	}

	@Override
	public OAuth2ServiceProvider getServiceProvider() {
		return Imgur.getInstance();
	}

	@Override
	public String getScope() {
		// TODO!
		// return "https://www.googleapis.com/auth/photoslibrary
		// https://www.googleapis.com/auth/photoslibrary.sharing";
		return null;
	}

	@Override
	public OAuthConnection getConnection() {
		if (connection == null) {
			connection = new OAuth2Connection(this);
		}
		return connection;
	}
	
	@Override
	public OAuth2AuthorizeResponseType defaultAuthorizeResponseType() {
		return OAuth2AuthorizeResponseType.TOKEN;
	}
}
