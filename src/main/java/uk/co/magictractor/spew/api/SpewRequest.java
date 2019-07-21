package uk.co.magictractor.spew.api;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.magictractor.spew.util.UrlEncoderUtil;

public final class SpewRequest {

    private final String httpMethod;
    private final String baseUrl;
    private final Map<String, Object> queryStringParams = new LinkedHashMap<>();
    private final Map<String, Object> bodyParams = new LinkedHashMap<>();

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
        this.baseUrl = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrl() {
        return baseUrl + UrlEncoderUtil.queryString("?", queryStringParams);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setQueryStringParam(String key, Object value) {
        if (value != null) {
            queryStringParams.put(key, value);
        }
        else {
            queryStringParams.remove(key);
        }
    }

    public void removeQueryStringParam(String key) {
        queryStringParams.remove(key);
    }

    public void setBodyParam(String key, Object value) {
        if (value != null) {
            bodyParams.put(key, value);
        }
        else {
            bodyParams.remove(key);
        }
    }

    public void removeBodyParam(String key) {
        bodyParams.remove(key);
    }

    public Object getQueryStringParam(String key) {
        // TODO! do not convert to empty string - do null handling where it's needed
        return queryStringParams.containsKey(key) ? queryStringParams.get(key) : "";
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    public Map<String, Object> getQueryStringParams() {
        return queryStringParams;
    }

}
