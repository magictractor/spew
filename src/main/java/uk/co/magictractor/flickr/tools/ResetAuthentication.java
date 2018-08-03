package uk.co.magictractor.flickr.tools;

import uk.co.magictractor.flickr.api.FlickrConfig;

public class ResetAuthentication {

	public static void main(String[] args) {
		FlickrConfig.resetUser();
	}
}
