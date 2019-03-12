package uk.co.magictractor.oauth.processor.properties;

public class OAuth2CachedResourceFileProperties extends CachedResourceFileProperties
		implements OAuthResourceFileProperties {

	public String getClientId() {
		return getProperty("clientId");
	}

	public String getClientSecret() {
		return getProperty("clientSecret");
	}

}
