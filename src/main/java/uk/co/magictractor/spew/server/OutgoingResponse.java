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
package uk.co.magictractor.spew.server;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.response.AbstractByteArrayMessage;
import uk.co.magictractor.spew.util.HttpMessageUtil;

/**
 * <p>
 * Simple responses are platform agnostic containers of information to be
 * displayed on a web page.
 * </p>
 * <p>
 * These can be used across multiple CallbackServer implementations.
 * </p>
 */
public abstract class OutgoingResponse extends AbstractByteArrayMessage implements SpewHttpResponse {

    private int httpStatus = 200;
    private List<SpewHeader> headers = new ArrayList<>();

    protected OutgoingResponse(Path bodyPath) {
        super(bodyPath);
        addHeader("Date", Instant.ofEpochMilli(System.currentTimeMillis()));
        // add Server header?
    }

    protected OutgoingResponse(byte[] bodyBytes) {
        super(bodyBytes);
    }

    protected OutgoingResponse(InputStream bodyStream) {
        super(bodyStream);
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public int getStatus() {
        return httpStatus;
    }

    @Override
    public List<SpewHeader> getHeaders() {
        return headers;
    }

    protected void addHeader(String name, String value) {
        headers.add(new SpewHeader(name, value));
    }

    protected void addHeader(String name, Instant value) {
        addHeader(name, HttpMessageUtil.asHeaderString(value));
    }

    // default visibility so the builder may access it, but the OutgoingResponse is otherwise immutable
    /* default */ void addHeaders(List<SpewHeader> addHeaders) {
        headers.addAll(addHeaders);
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }
}
