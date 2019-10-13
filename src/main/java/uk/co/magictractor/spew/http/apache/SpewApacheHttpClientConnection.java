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
package uk.co.magictractor.spew.http.apache;

import static uk.co.magictractor.spew.api.HttpHeaderNames.CONTENT_LENGTH;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.connection.AbstractSpewConnection;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class SpewApacheHttpClientConnection extends AbstractSpewConnection<SpewConnectionConfiguration> {

    public SpewApacheHttpClientConnection(SpewConnectionConfiguration configuration) {
        super(configuration);
    }

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest request) {
        /**
         * Some providers (including Twitter) log a warning related to the
         * format of the expiry date in cookies when the request config is not
         * modified.
         * <ul>
         * <li>https://stackoverflow.com/questions/36473478/fixing-httpclient-
         * warning-invalid-expires-attribute-using-fluent-api/40697322</li>
         * <li>https://issues.apache.org/jira/browse/HTTPCLIENT-1763</li>
         * </ul>
         */
        RequestConfig apacheRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(apacheRequestConfig)
                .build();

        RequestBuilder apacheRequestBuilder = RequestBuilder
                .create(request.getHttpMethod())
                .setUri(request.getUrl());

        byte[] body = request.getBodyBytes();
        boolean hasBody = body != null;
        if (hasBody) {
            apacheRequestBuilder.setEntity(new ByteArrayEntity(body));
        }

        for (SpewHeader header : request.getHeaders()) {
            if (hasBody && CONTENT_LENGTH.equalsIgnoreCase(header.getName())) {
                // Content length is derived from the HttpEntity (body), don't repeat it.
                continue;
            }
            apacheRequestBuilder.addHeader(header.getName(), header.getValue());
        }

        HttpUriRequest apacheRequest = apacheRequestBuilder.build();

        CloseableHttpResponse apacheResponse = ExceptionUtil.call(() -> httpClient.execute(apacheRequest));

        IncomingApacheHttpClientResponse response = new IncomingApacheHttpClientResponse(apacheResponse);

        // TODO! push logging up to abstract class
        getLogger().debug("request sent: {}", request);
        getLogger().debug("response received: {}", response);

        return response;
    }

}
