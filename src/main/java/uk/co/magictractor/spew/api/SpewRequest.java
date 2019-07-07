package uk.co.magictractor.spew.api;

import java.util.LinkedHashMap;
import java.util.Map;

public class SpewRequest {

    private final String httpMethod;
    private final String url;
    private final Map<String, Object> params = new LinkedHashMap<>();

    public static final SpewRequest createGetRequest(String url) {
        return new SpewRequest("GET", url);
    }

    public static final SpewRequest createPostRequest(String url) {
        return new SpewRequest("POST", url);
    }

    public static final SpewRequest createDelRequest(String url) {
        return new SpewRequest("DEL", url);
    }

    public static final SpewRequest createPutRequest(String url) {
        return new SpewRequest("PUT", url);
    }

    public static final SpewRequest createRequest(String httpMethod, String url) {
        return new SpewRequest(httpMethod, url);
    }

    private SpewRequest(String httpMethod, String url) {
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
