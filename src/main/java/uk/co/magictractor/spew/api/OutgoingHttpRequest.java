/**
 * Copyright 2019 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.spew.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uk.co.magictractor.spew.core.http.header.HasHttpHeaders;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 *
 */
public final class OutgoingHttpRequest implements SpewHttpRequest {

    private final String httpMethod;
    private final String path;
    private final Map<String, String> queryStringParams = new LinkedHashMap<>();
    private Map<String, String> unmodifiableQueryStringParams;
    private List<SpewHeader> headers = new ArrayList<>();
    private byte[] body;

    private boolean sent;

    // TODO! could be used with auth URLs which contain a question mark
    // => change the param to uri/url and parse if it contains a question mark
    public OutgoingHttpRequest(String httpMethod, String path) {
        this.httpMethod = httpMethod;
        this.path = path;
        if (path.contains("?")) {
            throw new IllegalStateException("query parameters should be added using setters");
        }
    }

    // TODO! remove this or mark this request as sent...
    private void ensureNotSent() {
        if (sent) {
            throw new IllegalStateException("request has already been sent");
        }
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Map<String, String> getQueryStringParams() {
        if (unmodifiableQueryStringParams == null) {
            unmodifiableQueryStringParams = Collections.unmodifiableMap(queryStringParams);
        }
        return unmodifiableQueryStringParams;
    }

    @Override
    public List<SpewHeader> getHeaders() {
        // TODO! unmodifiable?
        return headers;
    }

    @Override
    public byte[] getBodyBytes() {
        return body;
    }

    public void setHeaders(List<SpewHeader> headers) {
        ensureNotSent();
        this.headers.clear();
        this.headers.addAll(headers);
    }

    public void setQueryStringParams(Map<String, String> queryStringParams) {
        ensureNotSent();
        this.queryStringParams.clear();
        this.queryStringParams.putAll(queryStringParams);
    }

    public void setQueryStringParam(String key, String value) {
        ensureNotSent();
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        queryStringParams.put(key, value);
    }

    public void setQueryStringParam(String key, long value) {
        setQueryStringParam(key, Long.toString(value));
    }

    public void setHeader(String name, String value) {
        HasHttpHeaders.setHeader(headers, name, value);
    }

    public void setBody(byte[] body) {
        ensureNotSent();
        this.body = body;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(getPath());

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
            urlBuilder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }

        return urlBuilder.toString();
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
