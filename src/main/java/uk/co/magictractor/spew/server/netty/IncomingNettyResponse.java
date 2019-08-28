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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.HttpMessageUtil;

// no references to this??
public class IncomingNettyResponse implements SpewHttpResponse {

    private final FullHttpResponse nettyResponse;
    private List<SpewHeader> headers;

    public IncomingNettyResponse(FullHttpResponse nettyResponse) {
        this.nettyResponse = nettyResponse;
    }

    @Override
    public int getStatus() {
        return nettyResponse.status().code();
    }

    @Override
    public List<SpewHeader> getHeaders() {
        if (headers == null) {
            HttpHeaders nettyHeaders = nettyResponse.headers();
            headers = new ArrayList<>(nettyHeaders.size());
            for (Map.Entry<String, String> headerEntry : nettyHeaders.entries()) {
                headers.add(new SpewHeader(headerEntry.getKey(), headerEntry.getValue()));
            }
        }
        return headers;
    }

    @Override
    public InputStream getBodyInputStream() {
        return new ByteBufInputStream(nettyResponse.content());
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }

}
