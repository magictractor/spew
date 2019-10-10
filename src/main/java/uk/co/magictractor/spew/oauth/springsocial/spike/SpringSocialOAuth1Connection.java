/**
 * Copyright 2015-2019 Ken Dobson
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
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestOperations;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewOAuth1Configuration;
import uk.co.magictractor.spew.util.ExceptionUtil;

// https://docs.spring.io/spring-social/docs/current-SNAPSHOT/reference/htmlsingle/
public class SpringSocialOAuth1Connection implements SpewConnection {

    private final SpewOAuth1Configuration configuration;

    private RestOperations springOps;

    /**
     * Default visibility, applications should obtain instances via
     * {@link SpringSocialConnectionFactory#createConnection}, usually
     * indirectly via OAuthConnectionFactory.
     */
    /* default */ SpringSocialOAuth1Connection(SpewOAuth1Configuration configuration) {
        this.configuration = configuration;

        SpewOAuth1ConnectionFactory connectionFactory = new SpewOAuth1ConnectionFactory(configuration);

        OAuthToken token = new OAuthToken(
            configuration.getUserTokenProperty().getValue(), configuration.getUserSecretProperty().getValue());
        springOps = connectionFactory.createConnection(token).getApi();
    }

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
