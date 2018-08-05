package uk.co.magictractor.oauth.servers;

import uk.co.magictractor.oauth.api.OAuth1Server;

// https://www.flickr.com/services/apps/create/
public class Flickr implements OAuth1Server {

	public static final String REST_ENDPOINT = "https://api.flickr.com/services/rest/";

	@Override
	public String getTemporaryCredentialRequestUri() {
		return "https://www.flickr.com/services/oauth/request_token";
	}

	@Override
	public String getResourceOwnerAuthorizationUri() {
		// temporaryAuthToken added
		return "https://www.flickr.com/services/oauth/authorize?perms=write";
	}

	@Override
	public String getTokenRequestUri() {
		return "https://www.flickr.com/services/oauth/access_token";
	}

}
