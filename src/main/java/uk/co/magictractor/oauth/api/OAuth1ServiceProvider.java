package uk.co.magictractor.oauth.api;

public interface OAuth1ServiceProvider extends OAuthServiceProvider {

	// https://tools.ietf.org/html/rfc5849#section-1.1

//	   Temporary Credential Request
//       https://photos.example.net/initiate
//
// Resource Owner Authorization URI:
//       https://photos.example.net/authorize
//
// Token Request URI:
//       https://photos.example.net/token

	String getTemporaryCredentialRequestUri();

	String getResourceOwnerAuthorizationUri();

	String getTokenRequestUri();

	default String getSignatureMethod() {
		return "HMAC-SHA1";
	}

	// and consumer key
}
