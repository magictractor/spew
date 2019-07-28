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

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.util.ContentTypeUtil;
import uk.co.magictractor.spew.util.ExceptionUtil;

/**
 *
 */
public class ResourceResponse implements SpewResponse {

    private final Class<?> relativeToClass;
    private final String resourceName;
    private String contentType;
    private Optional<InputStream> bodyStream;

    public ResourceResponse(String resourceName) {
        this.relativeToClass = null;
        this.resourceName = resourceName;
    }

    public ResourceResponse(Class<?> testClass, String resourceName) {
        this.relativeToClass = testClass;
        this.resourceName = resourceName;
    }

    @Override
    public String getHeader(String headerName) {
        if (headerName.equalsIgnoreCase("Content-Type")) {
            return getContentType();
        }
        return null;
    }

    public String getContentType() {
        if (contentType == null) {
            contentType = determineContentType();
        }
        return contentType;
    }

    // TODO! split out code for determining content type from file name (perhaps with SPI)
    private String determineContentType() {
        return ContentTypeUtil.fromResourceName(resourceName);
    }

    private void ensureBodyInputStream() {
        if (bodyStream != null) {
            return;
        }

        //        if (relativeToClass == null) {
        //            bodyStream = Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(resourceName));
        //        }
        //        else {
        //            bodyStream = Optional.ofNullable(relativeToClass.getResourceAsStream(resourceName));
        //        }

        URL resource;
        if (relativeToClass == null) {
            resource = getClass().getClassLoader().getResource(resourceName);
        }
        else {
            resource = relativeToClass.getResource(resourceName);
        }

        if (resource == null) {
            System.err.println("resource does not exist with name " + resourceName);
            bodyStream = Optional.empty();
            return;
        }

        URI uri = ExceptionUtil.call(() -> resource.toURI());
        Path path = Paths.get(uri);

        if (Files.isDirectory(path)) {
            System.err.println("resource is a directory");
            bodyStream = Optional.empty();
            return;
        }

        bodyStream = Optional.of(ExceptionUtil.call(() -> resource.openStream()));
    }

    @Override
    public InputStream getBodyInputStream() {
        ensureBodyInputStream();
        return bodyStream.orElseThrow(() -> new IllegalStateException("resource not found"));
    }

    public boolean exists() {
        ensureBodyInputStream();
        return bodyStream.isPresent();
    }

}
