package uk.co.magictractor.oauth.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class OAuthRequest {

    private final String httpMethod;
    private final String url;
    private final Map<String, Object> params = new LinkedHashMap<>();

    public static final OAuthRequest createGetRequest(String url) {
        return new OAuthRequest("GET", url);
    }

    public static final OAuthRequest createPostRequest(String url) {
        return new OAuthRequest("POST", url);
    }

    public static final OAuthRequest createDelRequest(String url) {
        return new OAuthRequest("DEL", url);
    }

    public static final OAuthRequest createPutRequest(String url) {
        return new OAuthRequest("PUT", url);
    }

    public static final OAuthRequest createRequest(String httpMethod, String url) {
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
        }
        else {
            params.remove(key);
        }
    }

    public void removeParam(String key) {
        params.remove(key);
    }

    public Object getParam(String key) {
        // TODO! do not commit - do null handling where it's needed
        return params.containsKey(key) ? params.get(key) : "";
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
