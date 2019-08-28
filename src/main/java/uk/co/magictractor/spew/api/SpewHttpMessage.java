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
package uk.co.magictractor.spew.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.List;

import uk.co.magictractor.spew.util.HttpMessageUtil;

public interface SpewHttpMessage {

    /**
     * <p>
     * Note that header names should be case insensitive, but with some third
     * party libraries header names are case sensitive, in which case the
     * SpewResponse wrapper should work around the case sensitivity.
     * </p>
     *
     * @param headerName case insensitive header name
     * @return
     * @see https://stackoverflow.com/questions/5258977/are-http-headers-case-sensitive
     */
    default String getHeader(String headerName) {
        for (SpewHeader candidate : getHeaders()) {
            if (candidate.getName().equalsIgnoreCase(headerName)) {
                return candidate.getValue();
            }
        }
        return null;
    }

    List<SpewHeader> getHeaders();

    /**
     * <p>
     * A stream containing the body of the response, or null for responses
     * without a body. An empty InputStream may also be used for no body.
     * </p>
     * <p>
     * Multiple calls to this method should always return the same instance of
     * InputStream. TODO! unit tests for this
     * </p>
     * <p>
     * The InputStream returned should support marks (markSupported() returns
     * true). Implementations can do this by wrapping an underlying stream with
     * BufferedInputStream or similar if the underlying stream does not support
     * marks. This is to allow content sniffing to determine the content type
     * rather than relying only on the Content-Type header.
     * </p>
     *
     * @return a stream containing the body of the response, null for no body
     * @throws UncheckedIOException if an underlying IOException has to be
     *         handled
     */
    InputStream getBodyInputStream();

    /** @return body bytes, never null, may be empty */
    default byte[] getBodyBytes() {
        return HttpMessageUtil.getBodyBytes(this);
    }

    default ByteBuffer getBodyByteBuffer() {
        return HttpMessageUtil.getBodyByteBuffer(this);
    }

    default BufferedReader getBodyReader() {
        return HttpMessageUtil.getBodyReader(this);
    }

}
