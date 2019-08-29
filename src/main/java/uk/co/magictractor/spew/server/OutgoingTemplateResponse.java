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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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

    private final Function<String, String> valueFunction;

    private String keyStart = "${";
    private String keyEnd = "}";

    private Charset charset = StandardCharsets.UTF_8;

    // bin this? static equivalent was removed when if-modified-since was added
    public static OutgoingResponse ifExists(Class<?> relativeToClass, String resourceName,
            Function<String, String> valueFunction) {
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

        addHeader("cache-control", "no-cache, must-revalidate, max-age=0");
    }

    public OutgoingTemplateResponse(Path bodyPath, Function<String, String> valueFunction) {

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

        BufferedReader templateReader = HttpMessageUtil.getBodyReader(this, super.getBodyBytes());

        ByteArrayOutputStream pageBuilder = new ByteArrayOutputStream();

        String line = templateReader.readLine();
        while (line != null) {
            if (line.contains(keyStart)) {
                line = performSubstitutions(line);
            }

            pageBuilder.write(line.getBytes(charset));
            pageBuilder.write('\n');

            line = templateReader.readLine();
        }

        return pageBuilder.toByteArray();
    }

    private String performSubstitutions(String line) {
        StringBuilder substitutionBuilder = new StringBuilder();
        int fromIndex = 0;
        while (fromIndex != -1) {
            fromIndex = performSubstitutions(substitutionBuilder, line, fromIndex);
        }

        return substitutionBuilder.toString();
    }

    private int performSubstitutions(StringBuilder substitutionBuilder, String line, int fromIndex) {
        int startIndex = line.indexOf(keyStart, fromIndex);
        if (startIndex == -1) {
            substitutionBuilder.append(line.substring(fromIndex));
            return -1;
        }

        int endIndex = line.indexOf(keyEnd, startIndex + keyStart.length());
        if (endIndex == -1) {
            substitutionBuilder.append(line.substring(fromIndex));
            return -1;
        }

        String key = line.substring(startIndex + keyStart.length(), endIndex - keyEnd.length() + 1);
        String substitution = getSubstitutionValue(key);
        if (substitution == null) {
            System.err.println("no substitution value found for " + key);
            substitutionBuilder.append(line.substring(fromIndex, endIndex + 1));
            return endIndex + 1;
        }

        substitutionBuilder.append(line.substring(fromIndex, startIndex));
        substitutionBuilder.append(substitution);
        return endIndex + 1;
    }

    protected String getSubstitutionValue(String key) {
        return valueFunction.apply(key);
    }

}
