package uk.co.magictractor.oauth.flickr;

import uk.co.magictractor.oauth.api.OAuth1Application;
import uk.co.magictractor.oauth.api.OAuth1Connection;
import uk.co.magictractor.oauth.api.OAuth1ServiceProvider;
import uk.co.magictractor.oauth.api.OAuthConnection;

public class MyFlickrApp implements OAuth1Application {

	private static final MyFlickrApp INSTANCE = new MyFlickrApp();

// TODO! want connection cache in a superclass, not the file props
	private OAuth1Connection connection;

	private MyFlickrApp() {
	}

	public static MyFlickrApp getInstance() {
		return INSTANCE;
	}

	@Override
	public OAuth1ServiceProvider getServiceProvider() {
		return Flickr.getInstance();
	}

//	@Override
//	public String getScope() {
//		// return "https://www.googleapis.com/auth/photoslibrary";
//
//		// Do not need https://www.googleapis.com/auth/photoslibrary.sharing
//		// just use sharedAlbums/list to get list of shared albums
//		// Ah! do want album share info in order to get share Url
//		return "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing";
//
//		// just sharing results in permission denied when listing albums
//		// return "https://www.googleapis.com/auth/photoslibrary.sharing";
//	}

	@Override
	public OAuthConnection getConnection() {
		if (connection == null) {
			connection = new OAuth1Connection(this);
		}
		return connection;
	}
}
