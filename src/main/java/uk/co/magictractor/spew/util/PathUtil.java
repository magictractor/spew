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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

/**
 *
 */
public final class PathUtil {

    private PathUtil() {
    }

    public static Path regularFile(Class<?> relativeToClass, String resourceName) {
        return get(relativeToClass, resourceName, true);
    }

    public static Path ifExistsRegularFile(Class<?> relativeToClass, String resourceName) {
        return get(relativeToClass, resourceName, false);
    }

    private static Path get(Class<?> relativeToClass, String resourceName, boolean throwException) {
        URL resource;
        if (relativeToClass == null) {
            resource = PathUtil.class.getClassLoader().getResource(resourceName);
        }
        else {
            resource = relativeToClass.getResource(resourceName);
        }

        if (resource == null) {
            if (throwException) {
                throw new IllegalArgumentException("Resource not found: " + resourceName);
            }
            return null;
        }

        URI resourceUri = ExceptionUtil.call(() -> resource.toURI());
        Path path = Paths.get(resourceUri);

        if (Files.isDirectory(path)) {
            if (throwException) {
                throw new IllegalArgumentException("Resource exists but is a directory: " + path);
            }
            return null;
        }

        return path;
    }

    public static Instant getLastModified(Path path) {
        return ExceptionUtil.call(() -> Files.getLastModifiedTime(path)).toInstant();
    }

    public static byte[] read(Path path) {
        return ExceptionUtil.call(() -> read0(path));
    }

    public static byte[] read0(Path path) throws IOException {
        int size = (int) Files.size(path);
        byte[] bytes = new byte[size];
        try (InputStream inputStream = Files.newInputStream(path)) {
            inputStream.read(bytes);
        }
        return bytes;
    }

}
