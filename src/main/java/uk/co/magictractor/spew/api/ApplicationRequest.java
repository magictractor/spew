package uk.co.magictractor.spew.api;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.response.parser.jayway.JaywayConfigurationCache;
import uk.co.magictractor.spew.util.ContentTypeUtil;

// TODO! better name ApplicationRequest maybe? Parallel name with SpewParsedHttpResponse?
public final class ApplicationRequest {

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
    //private List<Consumer<OutgoingHttpRequest>> beforeSendConsumers;

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
    ApplicationRequest(SpewApplication<?> application, String httpMethod, String path) {
        this.application = application;
        this.httpMethod = httpMethod;
        if (path.contains("?")) {
            throw new IllegalStateException("query parameters should be added using setQueryStringParam()");
        }
        this.path = path;
    }

    public String getPath() {
        return path;
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

    public Optional<String> getQueryStringParam(String key) {
        return Optional.ofNullable(queryStringParams.get(key));
    }

    public byte[] getBody() {
        // TODO! maybe check if there are params but no body yet
        return bodyBytes;
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }

    public Map<String, String> getQueryStringParams() {
        return queryStringParams;
    }

    public void addHeader(String headerName, String headerValue) {
        headers.add(new SpewHeader(headerName, headerValue));
    }

    public void addHeader(String headerName, long headerValue) {
        addHeader(headerName, Long.toString(headerValue));
    }

    public void setHeader(String headerName, String headerValue) {
        HasHttpHeaders.setHeader(headers, headerName, headerValue);
    }

    public void setHeader(String headerName, long headerValue) {
        setHeader(headerName, Long.toString(headerValue));
    }

    //    @Override
    //    public SpewHttpResponse request(OutgoingHttpRequest request) {
    //        // TODO! add retry logic
    //        SpewHttpResponse response = super.request(request);
    //
    //        // aah... parse first to let Flickr etc modify status codes... !!!!
    //        if (response.getStatus() == 401) {
    //            getLogger().info("Existing authorization failed, obtaining fresh authorization");
    //            obtainAuthorization();
    //            response = super.request(request);
    //        }
    //
    //        // TODO! reauthorize (for 401? and retry for 5xx after a pause)
    //        // and do so for all connections...
    //        // Flickr has 200 status with error code 98 in body (could treat like 401 status)
    //        // and Flickr error code 105 could be treated like 5xx
    //        if (response.getStatus() != 200) {
    //            String message = "request returned status " + response.getStatus();
    //            System.err.println(message + ": " + request);
    //            System.err.println(response);
    //            throw new IllegalStateException(message);
    //        }
    //
    //        return response;
    //    }

    public SpewParsedResponse sendRequest() {
        // Maybe allow this?
        if (sent) {
            throw new IllegalStateException("This request has already been sent");
        }
        sent = true;

        prepareToSend();

        SpewParsedResponse response = sendOnce();

        if (response.getStatus() >= 400) {
            throw new IllegalStateException(
                "Code to be modified to handle 4xx and 5xx HTTP statuses, response has status " + response.getStatus());
        }

        return response;
    }

    private SpewParsedResponse sendOnce() {
        SpewConnection connection = application.getConnection();

        OutgoingHttpRequest request = build();

        // Add authentication.
        connection.prepareApplicationRequest(request);

        System.err.println("sending request: " + request);
        SpewHttpResponse response = connection.request(request);
        System.err.println("received response: " + response);

        SpewParsedResponseBuilder parsedResponseBuilder = new SpewParsedResponseBuilder(application, response);

        application.buildParsedResponse(parsedResponseBuilder);

        return parsedResponseBuilder.build();
    }

    public OutgoingHttpRequest build() {
        OutgoingHttpRequest request = new OutgoingHttpRequest(httpMethod, getPath());

        request.setQueryStringParams(getQueryStringParams());
        request.setHeaders(getHeaders());
        request.setBody(getBody());

        return request;
    }

    /**
     * Create body from body params and tidy up headers before request is sent.
     */
    private void prepareToSend() {
        // TODO! not just POST? PUT too
        if ("POST".equals(getHttpMethod())) {
            // setDoOutput(true);
            if (!getBodyParams().isEmpty()) {
                // TODO! allow body to have been set explicitly
                bodyBytes = ContentTypeUtil.bodyBytes(this, JaywayConfigurationCache.getConfiguration(application));
                setHeader(ContentTypeUtil.CONTENT_TYPE_HEADER_NAME, getContentType());
                setHeader(ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME, bodyBytes.length);
            }
            else {
                // Prevent 411 Content Length Required
                setHeader(ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME, 0);
            }
        }
        else if (!getBodyParams().isEmpty()) {
            // Move this check into setBodyParam()?
            throw new IllegalStateException("Body params not supported for HTTP method " + getHttpMethod());
        }
    }

    //    @Override
    //    public String toString() {
    //        ToStringHelper helper = HttpMessageUtil.toStringHelper(this);
    //        if (!bodyParams.isEmpty()) {
    //            helper.add("bodyParams", bodyParams);
    //        }
    //
    //        return helper.toString();
    //    }

}
