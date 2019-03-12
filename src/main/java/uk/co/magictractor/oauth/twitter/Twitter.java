package uk.co.magictractor.oauth.twitter;

import uk.co.magictractor.oauth.api.OAuth1ServiceProvider;

// manage apps at apps.twitter.com
public class Twitter implements OAuth1ServiceProvider {

	// https://developer.twitter.com/en/docs/basics/authentication/api-reference/request_token
	@Override
	public String getTemporaryCredentialRequestUri() {
		// TODO! should be POST
		return "https://api.twitter.com/oauth/request_token";
	}

	// https://developer.twitter.com/en/docs/basics/authentication/api-reference/authenticate
	@Override
	public String getResourceOwnerAuthorizationUri() {
		return "https://api.twitter.com/oauth/authenticate";
	}

	// https://developer.twitter.com/en/docs/basics/authentication/api-reference/access_token
	@Override
	public String getTokenRequestUri() {
		return "https://api.twitter.com/oauth/access_token";
	}

}
