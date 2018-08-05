package uk.co.magictractor.oauth.api;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.co.magictractor.oauth.util.ExceptionUtil;

public class OAuthRequest {

	// private String method;
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

//	public static OAuthRequest forAuth(String authMethod) {
//		return new OAuthRequest("https://www.flickr.com/services/oauth/" + authMethod);
//	}

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
