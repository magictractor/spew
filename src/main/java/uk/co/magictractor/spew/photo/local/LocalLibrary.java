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
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.photo.local.dates.DateRange;
import uk.co.magictractor.spew.photo.local.dates.LocalDirectoryDatesStrategy;
import uk.co.magictractor.spew.photo.local.files.PathIterator;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 *
 */
public class LocalLibrary {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalLibrary.class);

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

    // TODO! allow date search to be relaxed for finding images in tweets (based on subject and approx date)
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

    /**
     * <p>
     * Find the local copy of a remote photo.
     * </p>
     * <p>
     * It is likely that the remote photo was created by loading the local
     * photo. The remote photo is likely to lost the original file name, and is
     * likely to have been compressed. However, the date taken should allow for
     * a qucik and accurate match, and the dimensions of the image are used for
     * verification.
     * </p>
     * TODO! allow a machine tag containing the local file name
     */
    public LocalPhoto findLocalPhoto(Media remoteMedia) {
        LocalDate dateTaken = remoteMedia.getDateTaken();
        DateRange dateRange = DateRange.forDay(dateTaken);
        Predicate<Path> directoryFilter = (dir) -> directoryDatesStrategy.test(dir, dateRange);
        // TODO! more general (but not raw) - check TIF, GIF, JPG etc
        Predicate<Path> fileFilter = (file) -> file.toString().toLowerCase().endsWith(".jpg");
        PathIterator localIterator = iterator();
        localIterator.addDirectoryFilter(directoryFilter);
        localIterator.addFileFilter(fileFilter);

        Instant remoteDateTimeTaken = remoteMedia.getDateTimeTaken();
        List<LocalPhoto> localWithSameDateTaken = new ArrayList<>();
        //System.err.println("remote: " + remoteDateTimeTaken);
        while (localIterator.hasNext()) {
            Path localPath = localIterator.next();
            LocalPhoto candidate = new LocalPhoto(localPath);
            // Might need to allow for remote rounding to seconds etc.
            // 2019-10-31T13:15:05Z
            // 2019-10-31T13:15:05.324Z
            // TODO! something better (maybe include accuracy in Photo impl)
            if (candidate.getDateTimeTaken().with(ChronoField.MILLI_OF_SECOND, 0).equals(remoteDateTimeTaken)) {
                //System.err.println("candidate: " + candidate.getDateTimeTaken() + " " + localPath);
                localWithSameDateTaken.add(candidate);
            }
        }

        if (localWithSameDateTaken.isEmpty()) {
            throw new IllegalStateException("Failed to find local copy of " + remoteMedia);
        }
        else if (localWithSameDateTaken.size() > 1) {
            // TODO! this is OK (multiple crops of same image), should include in same group
            //throw new IllegalStateException("Multiple local photos matched " + remoteMedia);
            LOGGER.warn("Multiple local photos matched " + remoteMedia);
        }

        LocalPhoto result = localWithSameDateTaken.get(0);

        // TODO! additional verification: size

        LOGGER.debug("findLocalPhoto() found local file {}", result);

        return result;
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
