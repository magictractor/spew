package uk.co.magictractor.spew.api;

import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;

import org.slf4j.LoggerFactory;

public interface SpewResponse {

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
    String getHeader(String headerName);

    /**
     * <p>
     * A stream containing the body of the response.
     * </p>
     * TODO! establish whether this can only be read once - might be useful to
     * check body content when determining which parser to use
     *
     * @return a stream containing the body of the response
     * @throws UncheckedIOException if an underlying IOException has to be
     *         handled
     */
    InputStream getBodyStream();

    default String getContentType() {
        // TODO! simplify here and test case sensitivity in unit tests
        String upper = getHeader("Content-Type");
        String lower = getHeader("content-type");
        if (!Objects.deepEquals(upper, lower)) {
            LoggerFactory.getLogger(getClass()).error("getHeader() should be case insensitive");
        }

        String value = upper != null ? upper : lower;
        if (value == null) {
            throw new IllegalStateException("Response does not contain a Content-Type header");
        }

        // Strip out "charset=" etc
        int semiColonIndex = value.indexOf(";");
        if (semiColonIndex != -1) {
            value = value.substring(0, semiColonIndex).trim();
        }

        return value;
    }

}
