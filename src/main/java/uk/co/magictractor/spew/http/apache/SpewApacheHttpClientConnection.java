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

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class SpewApacheHttpClientConnection implements SpewConnection {

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest apiRequest) {
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
        RequestConfig requestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();

        RequestBuilder requestBuilder = RequestBuilder
                .create(apiRequest.getHttpMethod())
                .setUri(apiRequest.getUrl());

        boolean hasBody = apiRequest.getBody() != null;
        if (hasBody) {
            requestBuilder.setEntity(new ByteArrayEntity(apiRequest.getBody()));
        }

        for (SpewHeader header : apiRequest.getHeaders()) {
            System.err.println("header: " + header.getName() + "=" + header.getValue());
            if (hasBody && ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME.equalsIgnoreCase(header.getName())) {
                // Content length is derived from the HttpEntity (body), don't repeat it.
                continue;
            }
            requestBuilder.addHeader(header.getName(), header.getValue());
        }

        HttpUriRequest request = requestBuilder.build();

        CloseableHttpResponse response = ExceptionUtil.call(() -> httpClient.execute(request));

        return new IncomingApacheHttpClientResponse(response);
    }

}
