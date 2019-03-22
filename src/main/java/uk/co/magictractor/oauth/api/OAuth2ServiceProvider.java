package uk.co.magictractor.oauth.api;

public interface OAuth2ServiceProvider extends OAuthServiceProvider {

	String getAuthorizationUri();

	String getTokenUri();

//	String getTemporaryCredentialRequestUri();
//
//	String getResourceOwnerAuthorizationUri();
//
//	String getTokenRequestUri();

//	default String getSignatureMethod() {
//		return "HMAC-SHA1";
//	}

}