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

import static uk.co.magictractor.spew.api.HttpHeaderNames.DATE;

import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.HasHttpHeaders;
import uk.co.magictractor.spew.api.SpewHeader;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.core.message.AbstractByteArrayMessage;
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

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int httpStatus = 200;
    private List<SpewHeader> headers = new ArrayList<>();

    protected OutgoingResponse(Path bodyPath) {
        super(bodyPath);
        addHeader(DATE, Instant.ofEpochMilli(System.currentTimeMillis()));
        // add Server header?
    }

    protected OutgoingResponse(byte[] bodyBytes) {
        super(bodyBytes);
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

    protected void setHeader(String name, String value) {
        HasHttpHeaders.setHeader(headers, name, value);
    }

    protected void setHeader(String name, Instant value) {
        HasHttpHeaders.setHeader(headers, name, HttpMessageUtil.asHeaderString(value));
    }

    protected Logger getLogger() {
        return logger;
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this).toString();
    }
}
