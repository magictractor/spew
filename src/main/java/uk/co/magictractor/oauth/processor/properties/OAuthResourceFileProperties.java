package uk.co.magictractor.oauth.processor.properties;

public interface OAuthResourceFileProperties extends ResourceFileProperties {

	default String getResourceFolder() {
		return "oauth";
	}

}
