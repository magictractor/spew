package uk.co.magictractor.spew.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;

import uk.co.magictractor.spew.api.connection.SpewConnectionCache;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseFactory;
import uk.co.magictractor.spew.server.ServerRequest;
import uk.co.magictractor.spew.util.ContentTypeUtil;

public final class SpewRequest implements ServerRequest {

    private final SpewApplication application;

    private final String httpMethod;
    private final String baseUrl;
    private final Map<String, String> queryStringParams = new LinkedHashMap<>();
    private final Map<String, Object> bodyParams = new LinkedHashMap<>();
    // POST (and PUT) requests should have a content type
    // TODO! better to have this set explicitly and only once
    private String contentType = ContentTypeUtil.JSON_MIME_TYPES.get(0);

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

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String getUrl() {
        return getUrl(false);
    }

    public String getEscapedUrl() {
        return getUrl(true);
    }

    // TODO! do something better than the escapeValue param
    private String getUrl(boolean escapeValue) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(baseUrl);

        boolean first = true;
        for (Map.Entry<String, String> entry : queryStringParams.entrySet()) {
            if (first) {
                first = false;
                urlBuilder.append('?');
            }
            else {
                urlBuilder.append('&');
            }
            urlBuilder.append(entry.getKey());
            urlBuilder.append('=');
            if (escapeValue) {
                urlBuilder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
            }
            else {
                urlBuilder.append(entry.getValue());
            }
        }

        return urlBuilder.toString();
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        // TODO! restore check
        //        if (this.contentType != null) {
        //            throw new IllegalArgumentException("Request has already had a Content-Type set");
        //        }
        this.contentType = contentType;
    }

    public void setQueryStringParam(String key, Object value) {
        if (value != null) {
            queryStringParams.put(key, value.toString());
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

    @Override
    public String getQueryStringParam(String key) {
        // TODO! do not convert to empty string - do null handling where it's needed
        return queryStringParams.containsKey(key) ? queryStringParams.get(key) : "";
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    @Override
    public Map<String, String> getQueryStringParams() {
        return queryStringParams;
    }

    public SpewParsedResponse sendRequest() {
        if (sent) {
            throw new IllegalStateException("This request has already been sent");
        }
        SpewConnection connection = SpewConnectionCache.getConnection(application.getClass());
        SpewResponse response = connection.request(this);
        sent = true;

        SpewParsedResponse parsedResponse = SpewParsedResponseFactory.parse(application, response);

        application.getServiceProvider().verifyResponse(parsedResponse);

        return parsedResponse;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("url", getUrl())
                .add("bodyParams", getBodyParams())
                .toString();
    }

}
