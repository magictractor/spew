package uk.co.magictractor.flickr.api;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.co.magictractor.flickr.util.ExceptionUtil;

// https://www.flickr.com/services/api/request.rest.html
public class FlickrRequest {

	// also https://www.flickr.com/services/oauth/request_token
	// TODO could remove trailing slash??
	private static final String FLICKR_REST_ENDPOINT = "https://api.flickr.com/services/rest/";
	// private static final String SIGNATURE_METHOD = "HMAC-SHA1";
	private static final String SIGNATURE_METHOD = "HmacSHA1";

	// private String method;
	private final String httpMethod = "GET";
	private final String url;
	private final Map<String, String> params = new LinkedHashMap<>();

	private FlickrRequest(String url) {
		this.url = url;
		// https://www.flickr.com/services/api/response.json.html
		// setParam("format", "json");
		// setParam("nojsoncallback", "1");

		setParam("oauth_consumer_key", FlickrConfig.API_KEY);
		// TODO! nonce should be random, with guarantee that it is never the same if the
		// timestamp has not
		// move on since the last API call
		// TODO! how to make this testable (should be non-random during testing, but
		// Spring too heavyweight)
		// https://oauth.net/core/1.0a/#nonce
		addParam("oauth_nonce", new Random().nextInt());
		addParam("oauth_timestamp", System.currentTimeMillis());
		// setParam("oauth_callback", "www.google.com");
		// "oob" so that web shows the verifier which can then be copied
		setParam("oauth_callback", "oob");
		// addParam("oauth_signature_method", SIGNATURE_METHOD);
		setParam("oauth_version", "1.0");
		// TODO (tbc) method name not same in Java and API
		setParam("oauth_signature_method", "HMAC-SHA1");
		// request.addParam("oauth_signature", "7w18YS2bONDPL%2FzgyzP5XTr5af4%3D");
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public static FlickrRequest forAuth(String authMethod) {
		return new FlickrRequest("https://www.flickr.com/services/oauth/" + authMethod);
	}

	public static FlickrRequest forApi(String flickrMethod) {
		FlickrRequest request = new FlickrRequest(FLICKR_REST_ENDPOINT);
		request.setParam("api_key", FlickrConfig.API_KEY);
		request.setParam("method", flickrMethod);
		request.setParam("format", "json");
		request.setParam("nojsoncallback", "1");
		request.setParam("oauth_token", FlickrConfig.getUserAccessToken());

		return request;
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

	public String getUrl() {
		String unsignedUrl = getUrl(params);

		// urlBuilder.append("oauth_signature=");
		// urlBuilder.append(getSignature());

		return unsignedUrl + "&oauth_signature=" + getSignature();
	}

	private String getUrl(Map<String, String> p) {
		StringBuilder urlBuilder = new StringBuilder(url);
		urlBuilder.append("?");

		boolean isFirstParam = true;
		for (Entry<String, String> paramEntry : p.entrySet()) {
			if (isFirstParam) {
				isFirstParam = false;
			} else {
				urlBuilder.append("&");
			}
			urlBuilder.append(paramEntry.getKey());
			urlBuilder.append("=");
			urlBuilder.append(paramEntry.getValue());
			// urlBuilder.append("&");
		}

		return urlBuilder.toString();
	}

	// See https://www.flickr.com/services/api/auth.oauth.html
	// https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
	public String getSignature() {
		// TODO! consumer key only absent for auth
		// String key = FlickrConfig.API_KEY + "&";
		String key = FlickrConfig.API_SECRET + "&" + FlickrConfig.getUserSecret();
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), SIGNATURE_METHOD);
		Mac mac = ExceptionUtil.call(() -> Mac.getInstance(SIGNATURE_METHOD));
		ExceptionUtil.call(() -> mac.init(signingKey));

		String signature = Base64.getEncoder().encodeToString(mac.doFinal(getSignatureBaseString().getBytes()));
		return ExceptionUtil.call(() -> URLEncoder.encode(signature, "UTF-8"));
	}

	// See https://www.flickr.com/services/api/auth.oauth.html
	public String getSignatureBaseString() {
		String baseStringUrl = getUrl(getBaseStringParams());

		String encodedUrl = ExceptionUtil.call(() -> URLEncoder.encode(baseStringUrl, "UTF-8"));
		// meh, scruffy
		// replace the question mark with an ampersand
		encodedUrl = encodedUrl.replace("%3F", "&");

		return httpMethod + "&" + encodedUrl;
	}

	/** @return ordered params for building signature key */
	private Map<String, String> getBaseStringParams() {
		// TODO! some params should be ignored
		return new TreeMap<>(params);
	}
}
