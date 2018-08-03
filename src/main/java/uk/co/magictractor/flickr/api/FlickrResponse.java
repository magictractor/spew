package uk.co.magictractor.flickr.api;

public interface FlickrResponse {

	<T> T getObject(String key, Class<T> type);

	default Object getObject(String key) {
		return getObject(key, Object.class);
	}

	default String getString(String key) {
		return getObject(key, String.class);
	}
}
