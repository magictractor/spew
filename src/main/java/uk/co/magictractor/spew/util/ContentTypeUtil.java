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
package uk.co.magictractor.spew.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Splitter;

import uk.co.magictractor.spew.api.SpewResponse;

/**
 *
 */
public class ContentTypeUtil {

    public static final String CONTENT_TYPE_HEADER_NAME = "Content-Type";

    // TODO! private
    public static final List<String> JSON_MIME_TYPES = Arrays.asList("application/json");
    public static final List<String> HTML_MIME_TYPES = Arrays.asList("text/html");
    private static final List<String> CSS_MIME_TYPES = Arrays.asList("text/css");

    private static final List<String> JSON_EXTENSIONS = Arrays.asList("json");
    private static final List<String> HTML_EXTENSIONS = Arrays.asList("html", "htm");
    private static final List<String> CSS_EXTENSIONS = Arrays.asList("css");

    private ContentTypeUtil() {
    }

    public static boolean isJson(String contentType) {
        return JSON_MIME_TYPES.contains(contentType);
    }

    public static boolean isHtml(String contentType) {
        // TODO! beware appended ";charset=..."
        return HTML_MIME_TYPES.contains(contentType);
    }

    public static String fromHeader(SpewResponse response) {
        // TODO! simplify here and test case sensitivity in unit tests
        String upper = response.getHeader("Content-Type");
        String lower = response.getHeader("content-type");
        if (!Objects.deepEquals(upper, lower)) {
            // hmm, why doesn't this blow up? there was a workaround for this before...
            throw new IllegalStateException("getHeader() should be case insensitive");
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

    public static String fromBody(SpewResponse response) {
        byte[] bytes = new byte[4];

        InputStream bodyStream = response.getBodyInputStream();
        bodyStream.mark(bytes.length);
        try {
            bodyStream.read(bytes);
            bodyStream.reset();
        }
        catch (IOException e) {
            throw new UncheckedIOException("IOException using " + response.getClass().getSimpleName(), e);
        }

        if (bytes[0] == '{') {
            return JSON_MIME_TYPES.get(0);
        }

        // TODO! refer to better libraries would could be used
        throw new IllegalArgumentException(
            "Unable to determine the content type of the response by inspecting the body");
    }

    /**
     * <p>
     * Determine a content type based on the extension of the resource name.
     * </p>
     * <p>
     * When the resource name has multiple dots, each part of the extension,
     * split by dots is examined to determine the content type. This is to
     * handle name such as "mainPage.html.template".
     * </p>
     */
    public static String fromResourceName(String resourceName) {

        Iterable<String> parts = Splitter.on('.').split(resourceName.toLowerCase());
        Iterator<String> partIterator = parts.iterator();
        // Discard the first part which is the filename with extension stripped.
        partIterator.next();
        while (partIterator.hasNext()) {
            String contentType = fromExtensionPart(partIterator.next());
            if (contentType != null) {
                return contentType;
            }
        }

        throw new IllegalArgumentException(
            "Unable to determine the content type of the resource from name " + resourceName);

    }

    private static String fromExtensionPart(String extensionPart) {
        String contentType = null;
        if (HTML_EXTENSIONS.contains(extensionPart)) {
            contentType = HTML_MIME_TYPES.get(0);
        }
        else if (JSON_EXTENSIONS.contains(extensionPart)) {
            contentType = JSON_MIME_TYPES.get(0);
        }
        else if (CSS_EXTENSIONS.contains(extensionPart)) {
            contentType = CSS_MIME_TYPES.get(0);
        }

        return contentType;
    }

    public static Charset charsetFromHeader(SpewResponse response) {
        String header = response.getHeader(CONTENT_TYPE_HEADER_NAME);
        Charset charset = null;
        if (header != null) {
            int index = header.indexOf(";charset=");
            if (index != -1) {
                int startIndex = index + 9;
                int endIndex = header.indexOf(";", startIndex);
                String charsetName = endIndex == -1 ? header.substring(startIndex)
                        : header.substring(startIndex, endIndex);
                charset = Charset.forName(charsetName);
            }
        }

        return charset;
    }

}
