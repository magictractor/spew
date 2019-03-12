package uk.co.magictractor.oauth.google;

import uk.co.magictractor.oauth.api.OAuth2Application;
import uk.co.magictractor.oauth.api.OAuth2AuthorizeResponseType;
import uk.co.magictractor.oauth.api.OAuth2Connection;
import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;
import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.processor.properties.OAuth2CachedResourceFileProperties;

public class MyGooglePhotosApp extends OAuth2CachedResourceFileProperties implements OAuth2Application {

	private static final MyGooglePhotosApp INSTANCE = new MyGooglePhotosApp();

// TODO! want connection cache in a superclass, not the file props
	private OAuth2Connection connection;

	private MyGooglePhotosApp() {
	}

	public static MyGooglePhotosApp getInstance() {
		return INSTANCE;
	}

	@Override
	public OAuth2ServiceProvider getServiceProvider() {
		return Google.getInstance();
	}

	@Override
	public String getScope() {
		// return "https://www.googleapis.com/auth/photoslibrary";

		// Do not need https://www.googleapis.com/auth/photoslibrary.sharing
		// just use sharedAlbums/list to get list of shared albums
		// Ah! do want album share info in order to get share Url
		return "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing";

		// just sharing results in permission denied when listing albums
		// return "https://www.googleapis.com/auth/photoslibrary.sharing";
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
		return OAuth2AuthorizeResponseType.CODE;
	}
}
