package uk.co.magictractor.oauth.api;

public interface OAuthResponse {

	<T> T getObject(String key, Class<T> type);

	default Object getObject(String key) {
		return getObject(key, Object.class);
	}

	default String getString(String key) {
		return getObject(key, String.class);
	}
}
