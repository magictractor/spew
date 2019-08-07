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

import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class SpewApacheHttpClientConnection implements SpewConnection {

    @Override
    public SpewResponse request(SpewRequest apiRequest) {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        RequestBuilder requestBuilder = RequestBuilder
                .create(apiRequest.getHttpMethod())
                .setUri(apiRequest.getUrl());

        boolean hasBody = apiRequest.getBody() != null;
        if (hasBody) {
            requestBuilder.setEntity(new ByteArrayEntity(apiRequest.getBody()));
        }

        for (Map.Entry<String, String> headerEntry : apiRequest.getHeaders().entrySet()) {
            System.err.println("header: " + headerEntry.getKey() + "=" + headerEntry.getValue());
            if (hasBody && ContentTypeUtil.CONTENT_LENGTH_HEADER_NAME.equalsIgnoreCase(headerEntry.getKey())) {
                // Content length is derived from the HttpEntity (body), don't repeat it.
                continue;
            }
            requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
        }

        HttpUriRequest request = requestBuilder.build();

        CloseableHttpResponse response = ExceptionUtil.call(() -> httpClient.execute(request));

        return new SpewApacheHttpClientResponse(response);
    }

}
