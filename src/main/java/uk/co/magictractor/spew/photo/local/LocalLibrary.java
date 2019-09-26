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

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import uk.co.magictractor.spew.photo.local.dates.DateRange;
import uk.co.magictractor.spew.photo.local.dates.LocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.photo.local.files.PathIterator;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class LocalLibrary {

    private static LocalLibrary instance;

    private final List<Path> roots;
    private final LocalDirectoryDatesStrategy directoryDatesStrategy = SPIUtil
            .firstAvailable(LocalDirectoryDatesStrategy.class);

    public LocalLibrary(List<Path> roots) {
        this.roots = roots;
    }

    public PathIterator iterator() {
        return new PathIterator(roots);
    }

    public PathIterator iterator(DateRange dateRange) {
        PathIterator iterator = iterator();
        iterator.addDirectoryFilter(path -> directoryDatesStrategy.test(path, dateRange));
        return iterator;
    }

    public static LocalLibrary get() {
        if (instance == null) {
            synchronized (LocalLibrary.class) {
                if (instance == null) {
                    List<Path> roots = SPIUtil.available(LocalLibraryPathFinder.class)
                            .map(LocalLibraryPathFinder::libraryPaths)
                            .flatMap(Collection::stream)
                            .collect(Collectors.toList());
                    instance = new LocalLibrary(roots);
                }
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        LocalLibrary lib = LocalLibrary.get();
        DateRange range = DateRange.forMonth(2019, 9);
        PathIterator iter = lib.iterator(range);
        // iter.setFileFilter(LocalPhoto::isPhoto);
        System.err.println(iter.hasNext());
        while (iter.hasNext()) {
            System.err.println(iter.next());
        }
    }

}
