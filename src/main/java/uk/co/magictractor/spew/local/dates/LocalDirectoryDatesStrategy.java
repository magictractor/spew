package uk.co.magictractor.spew.local.dates;

import java.nio.file.Path;

public interface LocalDirectoryDatesStrategy {

    /**
     * May return null, but all photos in the directory would then need to be
     * opened to read the date taken from Exif.
     */
    DateRange getDateRange(String directoryName);

    default DateRange getDateRange(Path path) {
        return getDateRange(path.getFileName().toString());
    }

    default boolean test(Path path, DateRange dateRange) {
        DateRange directoryDateRange = getDateRange(path);

        System.err.println("date range for " + path + ": " + directoryDateRange);

        if (directoryDateRange == null) {
            // If the date range cannot be determined, return true so that subdirectories
            // and files within the directory will be inspected.
            return true;
        }

        return directoryDateRange.intersects(dateRange);
    }
}
