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
package uk.co.magictractor.spew.oauth.springsocial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;

import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.http.header.SpewHeader;
import uk.co.magictractor.spew.core.message.AbstractInputStreamMessage;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 *
 */
public class IncomingSpringSocialResponse extends AbstractInputStreamMessage implements SpewHttpResponse {

    private final ClientHttpResponse springResponse;
    private List<SpewHeader> headers;

    public IncomingSpringSocialResponse(ClientHttpResponse springResponse) throws IOException {
        super(() -> springResponse.getBody());
        this.springResponse = springResponse;
    }

    @Override
    public int getStatus() {
        return ExceptionUtil.call(() -> springResponse.getRawStatusCode());
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            HttpHeaders springHeaders = springResponse.getHeaders();
            headers = new ArrayList<>(springHeaders.size());
            for (Map.Entry<String, List<String>> springHeaderEntry : springHeaders.entrySet()) {
                String headerName = springHeaderEntry.getKey();
                for (String headerValue : springHeaderEntry.getValue()) {
                    headers.add(SpewHeader.of(headerName, headerValue));
                }
            }
        }
        return headers;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
