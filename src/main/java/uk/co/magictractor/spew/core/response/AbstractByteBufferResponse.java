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
package uk.co.magictractor.spew.core.response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

import uk.co.magictractor.spew.api.SpewResponse;

/**
 * Base class for response implementations which need to read and cache their
 * input stream because the third party library used for the connection will
 * automatically close the input stream.
 */
public abstract class AbstractByteBufferResponse implements SpewResponse {

    private final ByteArrayInputStream byteStream;

    protected AbstractByteBufferResponse(InputStream inputStream) throws IOException {
        this(ByteStreams.toByteArray(inputStream));
    }

    protected AbstractByteBufferResponse(byte[] bytes) {
        byteStream = new ByteArrayInputStream(bytes);
    }

    @Override
    public final InputStream getBodyStream() {
        return byteStream;
    }

}
