package uk.co.magictractor.oauth.google;

import uk.co.magictractor.oauth.api.OAuth2Application;
import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;

public class MyGooglePhotosApp implements OAuth2Application {

	private final OAuth2ServiceProvider serviceProvider = new Google();

	@Override
	public OAuth2ServiceProvider getServiceProvider() {
		return serviceProvider;
	}

	@Override
	public String getClientId() {
		return "346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com";
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

}
