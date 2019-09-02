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
package uk.co.magictractor.spew.server.netty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpRequest;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.core.message.AbstractInputStreamMessage;
import uk.co.magictractor.spew.server.SpewHttpRequest;
import uk.co.magictractor.spew.util.HttpMessageUtil;

public class IncomingNettyRequest extends AbstractInputStreamMessage implements SpewHttpRequest {

    private final FullHttpRequest request;
    private String path;
    private Map<String, String> queryStringParams;
    private List<SpewHeader> headers;

    public IncomingNettyRequest(FullHttpRequest request) {
        super(() -> new ByteBufInputStream(request.content()));
        this.request = request;
    }

    @Override
    public String getHttpMethod() {
        return request.method().name();
    }

    @Override
    public String getPath() {
        if (path == null) {
            String uri = request.uri();
            int qIndex = uri.indexOf("?");
            if (qIndex != -1) {
                path = uri.substring(0, qIndex);
            }
            else {
                path = uri;
            }
        }
        return path;
    }

    @Override
    public Map<String, String> getQueryStringParams() {
        if (queryStringParams == null) {
            String uri = request.uri();
            int qIndex = uri.indexOf("?");
            if (qIndex != -1) {
                String queryString = uri.substring(qIndex + 1);
                queryStringParams = Splitter.on('&').withKeyValueSeparator('=').split(queryString);
            }
            else {
                queryStringParams = Collections.emptyMap();
            }
        }
        return queryStringParams;
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            headers = request.headers()
                    .entries()
                    .stream()
                    .map(e -> new SpewHeader(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
        }
        return headers;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
