package uk.co.magictractor.oauth.api;

public interface OAuthConnection {

	OAuthResponse request(OAuthRequest apiRequest);

}
