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
package uk.co.magictractor.spew.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.io.ByteStreams;

import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.server.SpewHttpRequest;

/**
 *
 */
public final class HttpMessageUtil {

    private static final byte[] EMPTY_BODY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BODY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BODY_BYTES);

    private HttpMessageUtil() {
    }

    public static byte[] getBodyBytes(SpewHttpMessage httpMessage) {
        InputStream bodyInputStream = httpMessage.getBodyInputStream();
        if (bodyInputStream == null) {
            return EMPTY_BODY_BYTES;
        }

        return ExceptionUtil.call(() -> getBodyBytes0(bodyInputStream));
    }

    private static byte[] getBodyBytes0(InputStream bodyInputStream) throws IOException {

        // TODO! mark and reset
        // bodyInputStream.mark(readlimit); // hmm, need big read limit. Get size from headers?
        byte[] bytes = ByteStreams.toByteArray(bodyInputStream);

        return bytes;
    }

    public static ByteBuffer getBodyByteBuffer(SpewHttpMessage httpMessage) {
        if (httpMessage.getBodyInputStream() == null) {
            return EMPTY_BODY_BYTE_BUFFER;
        }

        return ByteBuffer.wrap(getBodyBytes(httpMessage));
    }

    public static BufferedReader getBodyReader(SpewHttpMessage httpMessage) {
        InputStream bodyStream = httpMessage.getBodyInputStream();
        if (bodyStream == null) {
            return null;
        }

        return getBodyReader(httpMessage, bodyStream);
    }

    public static BufferedReader getBodyReader(SpewHttpMessage httpMessage, InputStream bodyStream) {
        Charset charset = ContentTypeUtil.charsetFromHeader(httpMessage);
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new BufferedReader(new InputStreamReader(bodyStream, charset));
    }

    public static ToStringHelper toStringHelper(SpewHttpRequest request) {
        ToStringHelper helper = MoreObjects.toStringHelper(request)
                .add("path", request.getPath());
        if (!request.getQueryStringParams().isEmpty()) {
            helper.add("queryParams", request.getQueryStringParams());
        }

        return helper;
    }

    public static ToStringHelper toStringHelper(SpewHttpResponse response) {
        ToStringHelper helper = MoreObjects.toStringHelper(response)
                .add("status", response.getStatus());

        return helper;
    }

}
