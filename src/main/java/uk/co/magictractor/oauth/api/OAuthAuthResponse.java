package uk.co.magictractor.oauth.api;

import java.util.HashMap;
import java.util.Map;

//TODO! bin this?
public class OAuthAuthResponse implements OAuthResponse {

	private final Map<String, String> values = new HashMap<>();

	public OAuthAuthResponse(String response) {
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
