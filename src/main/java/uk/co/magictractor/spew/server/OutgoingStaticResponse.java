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
package uk.co.magictractor.spew.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.HttpMessageUtil;
import uk.co.magictractor.spew.util.PathUtil;

/**
 *
 */
// TODO! last modified time - available via Files
public class OutgoingStaticResponse extends OutgoingResponse {

    private final Path bodyPath;
    private String contentType;
    private InputStream bodyStream;

    public static OutgoingStaticResponse ifExists(Class<?> relativeToClass, String resourceName) {
        Path bodyPath = PathUtil.ifExistsRegularFile(relativeToClass, resourceName);
        return bodyPath != null ? new OutgoingStaticResponse(bodyPath) : null;
    }

    public OutgoingStaticResponse(Path bodyPath) {
        if (bodyPath == null) {
            throw new IllegalArgumentException("bodyPath must not be null");
        }
        this.bodyPath = bodyPath;
    }

    // bin??
    //    public String getContentType() {
    //        if (contentType == null) {
    //            contentType = determineContentType();
    //        }
    //        return contentType;
    //    }

    // TODO! split out code for determining content type from file name (perhaps with SPI)
    //    private String determineContentType() {
    //        return ContentTypeUtil.fromResourceName(resourceName);
    //    }

    @Override
    public final InputStream getBodyInputStream() {
        if (bodyStream == null) {
            bodyStream = ExceptionUtil.call(() -> createBodyInputStream(bodyPath));
        }
        return bodyStream;
    }

    protected InputStream createBodyInputStream(Path bodyPath) throws IOException {
        return Files.newInputStream(bodyPath);
    }

    @Override
    public String toString() {
        return HttpMessageUtil.toStringHelper(this)
                .add("bodyPath", bodyPath)
                .toString();
    }

}
