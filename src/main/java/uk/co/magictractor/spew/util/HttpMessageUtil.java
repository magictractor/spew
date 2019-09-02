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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.server.SpewHttpRequest;

/**
 *
 */
public final class HttpMessageUtil {

    private static final byte[] EMPTY_BODY_BYTES = new byte[0];
    private static final ByteBuffer EMPTY_BODY_BYTE_BUFFER = ByteBuffer.wrap(EMPTY_BODY_BYTES);

    private static final DateTimeFormatter HEADER_INSTANT_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT)
            .appendLiteral(", ")
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral(" GMT")
            .toFormatter();

    private HttpMessageUtil() {
    }

    public static InputStream createBodyInputStream(SpewHttpMessage httpMessage) {
        byte[] bodyBytes = httpMessage.getBodyBytes();
        if (bodyBytes == null || bodyBytes.length == 0) {
            return null;
        }

        return new ByteArrayInputStream(bodyBytes);
    }

    public static ByteBuffer createBodyByteBuffer(SpewHttpMessage httpMessage) {
        byte[] bodyBytes = httpMessage.getBodyBytes();
        if (bodyBytes == null || bodyBytes.length == 0) {
            return EMPTY_BODY_BYTE_BUFFER;
        }

        return ByteBuffer.wrap(bodyBytes);
    }

    public static BufferedReader createBodyReader(SpewHttpMessage httpMessage) {
        byte[] bodyBytes = httpMessage.getBodyBytes();
        if (bodyBytes == null || bodyBytes.length == 0) {
            return null;
        }

        return createBodyReader(httpMessage, bodyBytes);
    }

    // meh...
    public static BufferedReader createBodyReader(SpewHttpMessage httpMessage, byte[] bodyBytes) {
        Charset charset = ContentTypeUtil.charsetFromHeader(httpMessage);
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bodyBytes), charset));
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

    public static byte[] emptyBodyBytes() {
        return EMPTY_BODY_BYTES;
    }

    // Sun, 06 Nov 1994 08:49:37 GMT
    public static String asHeaderString(Instant instant) {
        OffsetDateTime t = instant.atOffset(ZoneOffset.UTC);
        return HEADER_INSTANT_FORMATTER.format(t);
    }

    /**
     * Assumes String is ASCII, characters in the String are cast to bytes
     * rather than being encoded via a Charset.
     */
    public static boolean bodyStartsWith(SpewHttpMessage message, String str) {
        byte[] bodyBytes = message.getBodyBytes();
        if (bodyBytes == null) {
            return false;
        }
        if (str.length() > bodyBytes.length) {
            return false;
        }

        for (int i = 0; i < str.length(); i++) {
            if (bodyBytes[i] != str.charAt(i)) {
                return false;
            }
        }

        return true;
    }

}
