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
package uk.co.magictractor.spew.server.undertow;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.server.SpewHttpRequest;

public class IncomingUndertowRequest implements SpewHttpRequest {

    private final HttpServerExchange undertowExchange;
    private List<SpewHeader> headers;
    private Map<String, String> queryParams;

    public IncomingUndertowRequest(HttpServerExchange undertowExchange) {
        this.undertowExchange = undertowExchange;
    }

    //    @Override
    //    public String getUrl() {
    //        String queryString = undertowExchange.getQueryString();
    //        return queryString.isEmpty() ? getBaseUrl() : getBaseUrl() + "?" + queryString;
    //    }

    @Override
    public String getPath() {
        return undertowExchange.getRequestPath();
    }

    @Override
    public Map<String, String> getQueryStringParams() {
        if (queryParams == null) {
            queryParams = new LinkedHashMap<>();
            for (Map.Entry<String, Deque<String>> queryEntry : undertowExchange.getQueryParameters().entrySet()) {
                if (queryEntry.getValue().size() != 1) {
                    throw new IllegalStateException(
                        "Code needs modified to handle multiple values for query string key " + queryEntry.getKey());
                }
                queryParams.put(queryEntry.getKey(), queryEntry.getValue().element());
            }
        }
        return queryParams;
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            HeaderMap undertowHeaders = undertowExchange.getRequestHeaders();
            headers = new ArrayList<>(undertowHeaders.size());
            long fiCookie = undertowHeaders.fastIterate();
            while (fiCookie != -1) {
                HeaderValues undertowHeaderValues = undertowHeaders.fiCurrent(fiCookie);
                for (int i = 0; i < undertowHeaderValues.size(); i++) {
                    headers.add(
                        new SpewHeader(undertowHeaderValues.getHeaderName().toString(), undertowHeaderValues.get(i)));
                }
            }
        }
        return headers;
    }
}
