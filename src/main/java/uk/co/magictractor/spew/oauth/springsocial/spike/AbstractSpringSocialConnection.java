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
package uk.co.magictractor.spew.oauth.springsocial.spike;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.connection.AbstractSpewConnection;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.util.ExceptionUtil;

public abstract class AbstractSpringSocialConnection<CONFIG extends SpewConnectionConfiguration>
        extends AbstractSpewConnection<CONFIG> {

    private final RestOperations springOps;

    protected AbstractSpringSocialConnection(CONFIG configuration) {
        super(configuration);
        springOps = init(configuration);
    }

    abstract RestOperations init(CONFIG configuration);

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest apiRequest) {
        // Convert to URI because execute(String, ...) escapes the String.
        URI uri = ExceptionUtil.call(() -> new URI(apiRequest.getUrl()));
        HttpMethod method = HttpMethod.valueOf(apiRequest.getHttpMethod());
        // RequestCallback requestCallback = System.err::println;
        RequestCallback requestCallback = httpRequest -> populateHttpRequest(httpRequest, apiRequest);
        SpewResponseHttpMessageConverter converter = new SpewResponseHttpMessageConverter();
        HttpMessageConverterExtractor<SpewHttpResponse> responseExtractor = new HttpMessageConverterExtractor<>(
            String.class, Arrays.asList(converter));

        return springOps.execute(uri, method, requestCallback, responseExtractor);
    }

    private void populateHttpRequest(ClientHttpRequest httpRequest, OutgoingHttpRequest apiRequest) throws IOException {

        HttpHeaders headers = httpRequest.getHeaders();
        for (SpewHeader header : apiRequest.getHeaders()) {
            headers.add(header.getName(), header.getValue());
        }

        byte[] body = apiRequest.getBodyBytes();
        if (body != null) {
            httpRequest.getBody().write(body);
        }
    }

}
