package uk.co.magictractor.oauth.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuthRequest {

	private final String url;
	private final Map<String, String> params = new LinkedHashMap<>();
	private String httpMethod = "GET";

	public OAuthRequest(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	// TODO! enum? infer from method (e.g. begins with "set")?
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setParam(String key, Number number) {
		params.put(key, number.toString());
	}

	public void setParam(String key, String value) {
		// Escape value to handle spaces in titles etc. - nope gubs oauth
		// params.put(key, UrlEncoderUtil.urlEncode(value));
		if (value != null) {
			params.put(key, value);
		} else {
			params.remove(key);
		}
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
