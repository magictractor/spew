package uk.co.magictractor.oauth.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuthRequest {

	private final String httpMethod;
	private final String url;
	private final Map<String, Object> params = new LinkedHashMap<>();

	public static final OAuthRequest get(String url) {
		return new OAuthRequest("GET", url);
	}

	public static final OAuthRequest post(String url) {
		return new OAuthRequest("POST", url);
	}

	public static final OAuthRequest del(String url) {
		return new OAuthRequest("DEL", url);
	}

	public static final OAuthRequest update(String url) {
		return new OAuthRequest("UPDATE", url);
	}

	public static final OAuthRequest create(String httpMethod, String url) {
		return new OAuthRequest(httpMethod, url);
	}

	private OAuthRequest(String httpMethod, String url) {
		this.httpMethod = httpMethod;
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public boolean hasParamsInBody() {
		// TODO! check whether DEL or other method types support a body
		return !"GET".equals(httpMethod);
	}

	public void setParam(String key, Object value) {
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

	public Object getParam(String key) {
		return params.get(key);
	}

	public Map<String, Object> getParams() {
		return params;
	}
}
