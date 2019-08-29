package uk.co.magictractor.spew.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.api.connection.SpewConnectionCache;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

public final class OutgoingHttpRequest implements SpewHttpRequest {

    private final SpewApplication<?> application;

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryStringParams = new LinkedHashMap<>();
    private final Map<String, Object> bodyParams = new LinkedHashMap<>();
    private byte[] bodyBytes;
    // POST (and PUT) requests should have a content type
    // TODO! better to have this set explicitly and only once
    private String contentType = ContentTypeUtil.JSON_MIME_TYPES.get(0);
    private List<SpewHeader> headers = new ArrayList<>();

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
    OutgoingHttpRequest(SpewApplication<?> application, String httpMethod, String path) {
        this.application = application;
        this.httpMethod = httpMethod;
        if (path.contains("?")) {
            throw new IllegalStateException("query parameters should be added using setQueryStringParam()");
        }
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    ///**
    // * @deprecated avoid using this - it's likely to get binned
    // */
    //    @Deprecated
    //    public String getUnescapedUrl() {
    //        return getUrl(false);
    //    }
    //

    //  @Override
    public String getUrl() {
        return getUrl(true);
    }

    // TODO! do something better than the escapeValue param
    @Deprecated
    private String getUrl(boolean escapeValue) {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(path);

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

    @Override
    public List<SpewHeader> getHeaders() {
        return headers;
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
    public Optional<String> getQueryStringParam(String key) {
        return Optional.ofNullable(queryStringParams.get(key));
    }

    @Override
    public byte[] getBodyBytes() {
        // TODO! maybe check if there are params but no body yet
        return bodyBytes;
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    @Override
    public Map<String, String> getQueryStringParams() {
        return queryStringParams;
    }

    public void addHeader(String headerName, String headerValue) {
        // TODO! reinstate check and provide method for adding another header with the same name
        //        if (headers.containsKey(headerName)) {
        //            throw new IllegalArgumentException(
        //                "Header already has a value assigned " + headerName + ": " + headers.get(headerName));
        //        }
        headers.add(new SpewHeader(headerName, headerValue));
    }

    public void addHeader(String headerName, long headerValue) {
        addHeader(headerName, Long.toString(headerValue));
    }

    public SpewParsedResponse sendRequest() {
        if (sent) {
            throw new IllegalStateException("This request has already been sent");
        }

        prepareToSend();

        SpewConnection connection = SpewConnectionCache.getOrCreateConnection(application.getClass());
        SpewHttpResponse response = connection.request(this);
        sent = true;

        SpewParsedResponse parsedResponse = SpewParsedResponse.parse(application, response);

        application.getServiceProvider().verifyResponse(parsedResponse);

        return parsedResponse;
    }

    /**
     * Create body from body params and tidy up headers before request is sent.
     */
    // TODO! not public - auth requests needing prepped too - maybe infer it?
    public void prepareToSend() {
        // TODO! not just POST? PUT too
        if ("POST".equals(getHttpMethod())) {
            // setDoOutput(true);
            if (!getBodyParams().isEmpty()) {
                // TODO! allow body to have been set explicitly
                bodyBytes = ContentTypeUtil.bodyBytes(this, application.getServiceProvider().getJsonConfiguration());
                addHeader(ContentTypeUtil.CONTENT_TYPE_HEADER_NAME, getContentType());
                addHeader(ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME, bodyBytes.length);
            }
            else {
                // Prevent 411 Content Length Required
                addHeader(ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME, 0);
            }
        }
        else if (!getBodyParams().isEmpty()) {
            // Move this check into setBodyParam()?
            throw new IllegalStateException("Body params not supported for HTTP method " + getHttpMethod());
        }
    }

    @Override
    public String toString() {
        ToStringHelper helper = HttpMessageUtil.toStringHelper(this);
        if (!bodyParams.isEmpty()) {
            helper.add("bodyParams", bodyParams);
        }

        return helper.toString();
    }

}
