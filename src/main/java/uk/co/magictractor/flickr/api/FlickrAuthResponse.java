package uk.co.magictractor.flickr.api;

import java.util.HashMap;
import java.util.Map;

public class FlickrAuthResponse implements FlickrResponse {

	private final Map<String, String> values = new HashMap<>();

	public FlickrAuthResponse(String response) {
		for (String entry : response.split("&")) {
			String[] keyAndValue = entry.split("=");
			values.put(keyAndValue[0], keyAndValue[1]);
		}
	}

	@Override
	public <T> T getObject(String key, Class<T> type) {
		// TODO! perhaps handle int/date conversions etc
		return (T) values.get(key);
	}

}
