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
import java.util.Map;

import com.google.common.base.Splitter;

import io.netty.handler.codec.http.FullHttpRequest;
import uk.co.magictractor.spew.server.ServerRequest;

public class FullHttpMessageRequest implements ServerRequest {

    private final FullHttpRequest request;
    private String baseUri;
    private Map<String, String> queryStringParams;

    public FullHttpMessageRequest(FullHttpRequest request) {
        this.request = request;
    }

    @Override
    public String getUrl() {
        return request.uri();
    }

    @Override
    public String getBaseUrl() {
        if (baseUri == null) {
            String uri = request.uri();
            int qIndex = uri.indexOf("?");
            if (qIndex != -1) {
                baseUri = uri.substring(0, qIndex);
            }
            else {
                baseUri = uri;
            }
        }
        return baseUri;
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
    public String getQueryStringParam(String paramName) {
        return getQueryStringParams().get(paramName);
    }

}
