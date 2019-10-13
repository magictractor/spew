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

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;

import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.core.message.AbstractInputStreamMessage;
import uk.co.magictractor.spew.util.HttpMessageUtil;

public class IncomingApacheHttpClientResponse extends AbstractInputStreamMessage implements SpewHttpResponse {

    private final CloseableHttpResponse response;
    private List<SpewHeader> headers;

    protected IncomingApacheHttpClientResponse(CloseableHttpResponse response) {
        super(() -> response.getEntity().getContent());
        this.response = response;
    }

    @Override
    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            Header[] apacheHeaders = response.getAllHeaders();
            headers = new ArrayList<>(apacheHeaders.length);
            for (Header apacheHeader : apacheHeaders) {
                headers.add(new ApacheHeader(apacheHeader));
            }
        }
        return headers;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
