package uk.co.magictractor.spew.api;

public interface OAuth2ServiceProvider extends SpewServiceProvider {

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
