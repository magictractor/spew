package uk.co.magictractor.oauth.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuthRequest {

	private final String httpMethod = "GET";
	private final String url;
	private final Map<String, String> params = new LinkedHashMap<>();

	public OAuthRequest(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void addParam(String key, Number number) {
		params.put(key, number.toString());
	}

	public void setParam(String key, String value) {
		params.put(key, value);
	}

	public void removeParam(String key) {
		params.remove(key);
	}

	public String getParam(String key) {
		return params.get(key);
	}

	// TODO! remove this??
	public Map<String, String> getParams() {
		return params;
	}
}
