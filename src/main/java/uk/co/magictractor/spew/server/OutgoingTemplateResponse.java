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

import static uk.co.magictractor.spew.api.HttpHeaderNames.CACHE_CONTROL;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;

import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;
import uk.co.magictractor.spew.util.PathUtil;

/**
 * Simple representation of an HTML page with text substitutions made for values
 * in the template delimited by curly braces.
 */
public class OutgoingTemplateResponse extends OutgoingResponse {

    private static String suffix = ".template";

    private final Function<String, Object> valueFunction;

    private String keyStart = "${";
    private String keyEnd = "}";

    private Charset charset = StandardCharsets.UTF_8;

    // bin this? static equivalent was removed when if-modified-since was added
    public static OutgoingResponse ifExists(Class<?> relativeToClass, String resourceName,
            Function<String, Object> valueFunction) {
        Path bodyPath = PathUtil.ifExistsRegularFile(relativeToClass, resourceName + suffix);

        if (bodyPath != null) {
            return new OutgoingTemplateResponse(bodyPath, valueFunction);
        }
        else if (resourceName.endsWith(suffix)) {
            // Prevent templates being rendered as static files when templates are handled before static files.
            return OutgoingErrorResponse.notFound();
        }

        return null;
    }

    protected OutgoingTemplateResponse(String resourceName) {
        this(PathUtil.regularFile(CallbackServer.class, resourceName + suffix));
    }

    public OutgoingTemplateResponse(Path bodyPath) {
        super(bodyPath);

        this.valueFunction = (key) -> {
            throw new IllegalStateException(
                "getSubstitutionValue() must be overridden if a value function is not provided");
        };

        setHeader(CACHE_CONTROL, "no-cache, must-revalidate, max-age=0");
    }

    public OutgoingTemplateResponse(Path bodyPath, Function<String, Object> valueFunction) {

        super(bodyPath);

        if (valueFunction == null) {
            throw new IllegalArgumentException("valueFunction must not be null");
        }
        this.valueFunction = valueFunction;

        // header...
    }

    @Override
    public byte[] getBodyBytes() {
        return ExceptionUtil.call(() -> getBodyBytes0());
    }

    private byte[] getBodyBytes0() throws IOException {

        BufferedReader templateReader = HttpMessageUtil.createBodyReader(this, super.getBodyBytes());

        ByteArrayOutputStream pageStream = new ByteArrayOutputStream();
        Writer pageWriter = new OutputStreamWriter(pageStream, charset);

        String line = templateReader.readLine();
        while (line != null) {
            renderLine(pageWriter, line);
            pageWriter.write('\n');

            line = templateReader.readLine();
        }

        pageWriter.close();
        // and pageStream.close(); ?
        return pageStream.toByteArray();
    }

    private void renderLine(Writer out, String line) throws IOException {
        int fromIndex = 0;
        while (fromIndex != -1) {
            fromIndex = renderLineFrom(out, line, fromIndex);
        }
    }

    private int renderLineFrom(Writer out, String line, int fromIndex) throws IOException {
        int startIndex = line.indexOf(keyStart, fromIndex);
        if (startIndex == -1) {
            out.write(line.substring(fromIndex));
            return -1;
        }

        int endIndex = line.indexOf(keyEnd, startIndex + keyStart.length());
        if (endIndex == -1) {
            out.append(line.substring(fromIndex));
            return -1;
        }

        out.append(line.substring(fromIndex, startIndex));
        String key = line.substring(startIndex + keyStart.length(), endIndex - keyEnd.length() + 1);

        boolean substituted = renderSubstitutionValue(out, key);
        if (!substituted) {
            // TODO! log line and column
            getLogger().warn("No substitution value found for " + key);
            out.append(line.substring(startIndex, endIndex + 1));
        }

        return endIndex + 1;
    }

    /**
     * @return value indicating whether a value for the key was found
     */
    protected boolean renderSubstitutionValue(Writer out, String key) throws IOException {
        String valueKey;
        String renderHint = null;
        int colonIndex = key.indexOf(":");
        if (colonIndex == -1) {
            valueKey = key;
        }
        else {
            valueKey = key.substring(0, colonIndex);
            renderHint = key.substring(colonIndex + 1);
        }

        Object value = getSubstitutionValue(valueKey);
        if (value == null) {
            return false;
        }

        render(out, value);

        return true;
    }

    // May be overridden.
    protected Object getSubstitutionValue(String valueKey) {
        return valueFunction.apply(valueKey);
    }

    protected void render(Writer out, Object value) throws IOException {
        // TODO! SPI
        if (value instanceof Map) {
            renderMap(out, (Map) value);
        }
        else {
            out.write(value.toString());
        }
    }

    private void renderMap(Writer out, Map<?, ?> map) throws IOException {
        out.write("<div class=\"table\">");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            out.write("<div class=\"row\">");
            out.write("<span class=\"cell\">");
            render(out, entry.getKey());
            out.write("</span>");
            out.write("<span class=\"cell\">");
            render(out, entry.getValue());
            out.write("</span>");
            out.write("</div>");
        }
        out.write("</div>");
    }

}
