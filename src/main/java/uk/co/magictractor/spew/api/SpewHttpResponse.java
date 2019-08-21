package uk.co.magictractor.spew.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.ByteStreams;

import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;

public interface SpewHttpResponse {

    int getStatus();

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
     * without a body. An empty could also be used for no body.
     * </p>
     * <p>
     * Multiple calls to this method should always return the same instance of
     * InputStream.
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

    default byte[] getBodyBytes() {
        if (getBodyInputStream() == null) {
            return null;
        }

        return ExceptionUtil.call(() -> ByteStreams.toByteArray(getBodyInputStream()));
    }

    default ByteBuffer getBodyByteBuffer() {
        if (getBodyInputStream() == null) {
            return null;
        }

        return ByteBuffer.wrap(getBodyBytes());
    }

    default BufferedReader getBodyReader() {
        if (getBodyInputStream() == null) {
            return null;
        }

        Charset charset = ContentTypeUtil.charsetFromHeader(this);
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        return new BufferedReader(new InputStreamReader(getBodyInputStream(), charset));
    }

}
