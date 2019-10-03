package uk.co.magictractor.spew.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.api.connection.SpewConnectionCache;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.core.response.parser.jayway.JaywayConfigurationCache;
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
    private List<Consumer<OutgoingHttpRequest>> beforeSendConsumers;

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

    @Override
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
        headers.add(new SpewHeader(headerName, headerValue));
    }

    public void addHeader(String headerName, long headerValue) {
        addHeader(headerName, Long.toString(headerValue));
    }

    public void setHeader(String headerName, String headerValue) {
        //headers.add(new SpewHeader(headerName, headerValue));
        int existingIndex = -1;
        int i = -1;
        for (SpewHeader header : headers) {
            i++;
            if (header.getName().equalsIgnoreCase(headerName)) {
                if (existingIndex == -1) {
                    existingIndex = i;
                }
                else {
                    throw new IllegalStateException("There are multiple existing headers with name " + headerName);
                }
            }
        }

        SpewHeader header = new SpewHeader(headerName, headerValue);
        if (existingIndex == -1) {
            headers.add(header);
        }
        else {
            headers.set(existingIndex, header);
        }
    }

    public void setHeader(String headerName, long headerValue) {
        setHeader(headerName, Long.toString(headerValue));
    }

    public SpewParsedResponse sendRequest() {
        if (sent) {
            throw new IllegalStateException("This request has already been sent");
        }

        prepareToSend();

        SpewConnection connection = SpewConnectionCache.getOrCreateConnection(application.getClass());

        System.err.println("sending request: " + this);

        SpewHttpResponse response = connection.request(this);

        System.err.println("received response: " + response);

        SpewParsedResponseBuilder parsedResponseBuilder = new SpewParsedResponseBuilder(application, response);

        // TODO! move error handling here? or move where parsing is done??

        sent = true;

        //SpewHttpMessageBodyReader parsedResponse = SpewHttpMessageBodyReader.parse(application, response);
        //application.getServiceProvider().verifyResponse(parsedResponse);

        // TODO! retry/reauthenticate AFTER building
        application.getServiceProvider().buildParsedResponse(parsedResponseBuilder);

        return parsedResponseBuilder.build();
    }

    public void beforeSend(Consumer<OutgoingHttpRequest> beforeSendConsumer) {
        if (beforeSendConsumers == null) {
            beforeSendConsumers = new ArrayList<>();
        }
        beforeSendConsumers.add(beforeSendConsumer);
    }

    /**
     * Create body from body params and tidy up headers before request is sent.
     */
    // TODO! not public - auth requests needing prepped too - maybe infer it?
    public void prepareToSend() {
        if (beforeSendConsumers != null) {
            beforeSendConsumers.forEach(consumer -> consumer.accept(this));
        }

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

    @Override
    public String toString() {
        ToStringHelper helper = HttpMessageUtil.toStringHelper(this);
        if (!bodyParams.isEmpty()) {
            helper.add("bodyParams", bodyParams);
        }

        return helper.toString();
    }

}
