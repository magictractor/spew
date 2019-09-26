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
package uk.co.magictractor.spew.photo.local;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.photo.local.dates.LocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.util.ExceptionUtil;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class DefaultLocalLibraryPathFinder implements LocalLibraryPathFinder {

    @Override
    public List<Path> libraryPaths() {
        String home = System.getProperty("user.home");
        if (home == null) {
            throw new IllegalStateException("Missing user.home property");
        }

        Path homePath = Paths.get(home);
        if (!Files.exists(homePath)) {
            throw new IllegalStateException(homePath + " does not exist");
        }

        Path picturesPath = Paths.get(home, "Pictures");
        if (!Files.exists(picturesPath)) {
            throw new IllegalStateException(picturesPath + " does not exist");
        }

        // In my collection, this limits the library to Pictures/{year} directories.
        List<Path> libraryPaths = subDirectoriesWithDateRanges(picturesPath);
        if (libraryPaths.isEmpty()) {
            libraryPaths = Collections.singletonList(picturesPath);
        }

        return libraryPaths;
    }

    private List<Path> subDirectoriesWithDateRanges(Path directory) {
        LocalDirectoryDatesStrategy directoryDatesStrategy = SPIUtil
                .firstAvailable(LocalDirectoryDatesStrategy.class);

        return ExceptionUtil.call(() -> Files.list(directory))
                .filter(Files::isDirectory)
                .filter(subDirectory -> directoryDatesStrategy.getDateRange(subDirectory) != null)
                .collect(Collectors.toList());
    }

}
