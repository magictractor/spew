package uk.co.magictractor.spew.api;

import java.util.LinkedHashMap;
import java.util.Map;

import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseFactory;

public final class SpewRequest {

    private final SpewApplication application;

    private final String httpMethod;
    private final String baseUrl;
    private final Map<String, Object> queryStringParams = new LinkedHashMap<>();
    private final Map<String, Object> bodyParams = new LinkedHashMap<>();

    private boolean sent;

    /**
     * <p>
     * SpewRequest instances should be obtained via SpewApplication to ensure
     * that default params are set on every request for the application and/or
     * service provider.
     * </p>
     * </p>
     * For example, the Flickr service provider will create a SpewRequest which
     * has "format" and "nojsoncallback" params set for every request.
     * </p>
     *
     * <pre>
     * request.setQueryStringParam("format", "json");
     * request.setQueryStringParam("nojsoncallback", "1");
     * </pre>
     */
    /* default */ SpewRequest(SpewApplication application, String httpMethod, String url) {
        this.application = application;
        this.httpMethod = httpMethod;
        this.baseUrl = url;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);

        boolean first = true;
        for (Map.Entry<String, Object> entry : queryStringParams.entrySet()) {
            if (first) {
                first = false;
                urlBuilder.append('?');
            }
            else {
                urlBuilder.append('&');
            }
            urlBuilder.append(entry.getKey());
            urlBuilder.append('=');
            urlBuilder.append(entry.getValue());
        }

        return urlBuilder.toString();
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

    public SpewParsedResponse sendRequest() {
        if (sent) {
            throw new IllegalStateException("This request has already been sent");
        }
        SpewConnection connection = SpewConnectionFactory.getConnection(application.getClass());
        SpewResponse response = connection.request(this);
        sent = true;

        SpewParsedResponse parsedResponse = SpewParsedResponseFactory.parse(application, response);

        application.getServiceProvider().verifyResponse(parsedResponse);

        return parsedResponse;
    }

}
