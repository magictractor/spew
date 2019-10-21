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

import static uk.co.magictractor.spew.api.HttpHeaderNames.CONTENT_TYPE;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.jayway.jsonpath.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SpewHttpMessage;
import uk.co.magictractor.spew.core.contenttype.ContentTypeFromResourceName;
import uk.co.magictractor.spew.core.http.header.HasHttpHeaders;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class ContentTypeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContentTypeUtil.class);

    // TODO! private
    // TODO! Dropbox auth uses text/javascript in the token response. Tolerate here for now, but perhaps move to an override in the app?
    public static final List<String> JSON_MIME_TYPES = Arrays.asList("application/json", "text/javascript");
    public static final List<String> XML_MIME_TYPES = Arrays.asList("application/xml", "text/xml");
    public static final List<String> HTML_MIME_TYPES = Arrays.asList("text/html");
    private static final List<String> CSS_MIME_TYPES = Arrays.asList("text/css");
    private static final List<String> JPEG_MIME_TYPES = Arrays.asList("image/jpeg");
    // Auth requests should use this rather than JSON. Some service providers tolerate JSON for auth.
    public static final String FORM_MIME_TYPE = "application/x-www-form-urlencoded";

    private static final List<String> JSON_EXTENSIONS = Arrays.asList("json");
    private static final List<String> HTML_EXTENSIONS = Arrays.asList("html", "htm");
    private static final List<String> CSS_EXTENSIONS = Arrays.asList("css");
    private static final List<String> JPEG_EXTENSIONS = Arrays.asList("jpg", "jpeg");

    private ContentTypeUtil() {
    }

    public static boolean isJson(String contentType) {
        // TODO! beware appended ";charset=..."
        return JSON_MIME_TYPES.contains(contentType);
    }

    public static boolean isXml(String contentType) {
        // TODO! beware appended ";charset=..."
        return XML_MIME_TYPES.contains(contentType);
    }

    public static boolean isHtml(String contentType) {
        // TODO! beware appended ";charset=..."
        return HTML_MIME_TYPES.contains(contentType);
    }

    public static boolean isBinary(String contentType) {
        // TODO! beware appended ";charset=..."
        if (contentType.startsWith("text/")) {
            return false;
        }
        if (contentType.endsWith("/json")) {
            return false;
        }
        return true;
    }

    public static String fromHeader(HasHttpHeaders hasHeaders) {
        String contentType = hasHeaders.getHeaderValue(CONTENT_TYPE);

        if (contentType == null) {
            throw new IllegalStateException("Response does not contain a Content-Type header");
        }

        // Strip out "charset=" etc
        int semiColonIndex = contentType.indexOf(";");
        if (semiColonIndex != -1) {
            contentType = contentType.substring(0, semiColonIndex).trim();
        }

        return contentType;
    }

    /**
     * <p>
     * Determine a content type based on the extension of the resource name.
     * </p>
     * <p>
     * When the resource name has multiple dots, each part of the extension,
     * split by dots is examined to determine the content type. This is to
     * handle name such as "mainPage.html.template". TODO! yuck??
     * </p>
     */
    public static String fromResourceName(String resourceName) {
        return SPIUtil
                .firstNotNull(ContentTypeFromResourceName.class, t -> t.determineContentType(resourceName))
                .orElseGet(() -> {
                    LOGGER.warn("Unable to determine content type for resource name " + resourceName);
                    return "application/octet-stream";
                });
    }

    public static Optional<Charset> charsetFromHeader(SpewHttpMessage httpMessage) {
        String header = httpMessage.getHeaderValue(CONTENT_TYPE);
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

        return Optional.ofNullable(charset);
    }

    // TODO! Jayway param - yuck
    public static byte[] bodyBytes(ApplicationRequest request, Configuration jsonConfiguration) {
        String contentType = request.getContentType();
        if (isJson(contentType)) {
            return bodyJsonBytes(request, jsonConfiguration);
        }
        else if (FORM_MIME_TYPE.equals(contentType)) {
            return bodyFormBytes(request);
        }
        else if (null == contentType) {
            throw new IllegalArgumentException(
                "Requests with body params must have a content type so that params are encoded appropriately");
        }
        throw new IllegalArgumentException("Code need modified to write request body for content type " + contentType);
    }

    private static byte[] bodyJsonBytes(ApplicationRequest request, Configuration jsonConfiguration) {
        String requestBody = jsonConfiguration.jsonProvider().toJson(request.getBodyParams());
        //logger.trace("request body: " + requestBody);
        // TODO! encoding
        return requestBody.getBytes();
    }

    private static byte[] bodyFormBytes(ApplicationRequest request) {
        StringBuilder bodyBuilder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : request.getBodyParams().entrySet()) {
            if (first) {
                first = false;
            }
            else {
                // Some examples online look there could be a newline, but there must not.
                bodyBuilder.append('&');
            }
            bodyBuilder.append(entry.getKey());
            bodyBuilder.append('=');
            bodyBuilder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }

        // TODO! is UTF-8 correct?
        return bodyBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

}
