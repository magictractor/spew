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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import uk.co.magictractor.spew.core.response.ResourceResponse;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 * Simple representation of an HTML page with text substitutions made for values
 * in the template delimited by curly braces.
 */
public class SimpleTemplateResponse extends SimpleResponse implements SimpleResourceResponse {

    private final ResourceResponse spewResponse;
    private final Map<String, String> substitutionMap = new HashMap<>();

    private String keyStart = "{";
    private String keyEnd = "}";

    private Charset charset = StandardCharsets.UTF_8;

    public static SimpleTemplateResponse ifExists(Class<?> relativeToClass, String resourceName) {
        ResourceResponse resourceResponse = new ResourceResponse(relativeToClass, resourceName);
        return resourceResponse.exists() ? new SimpleTemplateResponse(resourceResponse) : null;
    }

    public SimpleTemplateResponse(ResourceResponse spewResponse) {
        if (!spewResponse.exists()) {
            throw new IllegalArgumentException("Resource does not exist");
        }
        this.spewResponse = spewResponse;
    }

    public SimpleTemplateResponse(Class<?> relativeToClass, String resourceName) {
        this(new ResourceResponse(relativeToClass, resourceName));
    }

    public void addSubstitution(String key, Object value) {
        substitutionMap.put(keyStart + key + keyEnd, value.toString());
    }

    @Override
    public String getHeader(String headerName) {
        return spewResponse.getHeader(headerName);
    }

    @Override
    public InputStream getBodyInputStream() {
        return ExceptionUtil.call(() -> getBodyInputStream0());
    }

    private InputStream getBodyInputStream0() throws IOException {
        BufferedReader templateReader = spewResponse.getBodyReader();

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

        return new ByteArrayInputStream(pageBuilder.toByteArray());
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

        String key = line.substring(startIndex, endIndex + 1);
        String substitution = substitutionMap.get(key);
        if (substitution == null) {
            System.err.println("no substitution value found for " + key);
            substitutionBuilder.append(line.substring(fromIndex, endIndex + 1));
            return endIndex + 1;
        }

        substitutionBuilder.append(line.substring(fromIndex, startIndex));
        substitutionBuilder.append(substitution);
        return endIndex + 1;
    }

}
