package uk.co.magictractor.spew.photo.local.dates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLocalDirectoryDatesStrategy implements LocalDirectoryDatesStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultLocalDirectoryDatesStrategy.class);

    /**
     * <ul>
     * <li>4 digits exactly assumed to be year, such as 2019</li>
     * <li>6 digits assumed to be year and zero padded month such as 201907</li>
     * <li>8 digits assumed to be year, zero padded month and zero padded day,
     * such as 20190123</li>
     */
    @Override
    public DateRange getDateRange(String directoryName) {
        String dateString = getDateString(directoryName);

        // Check for other digits
        int n = directoryName.length() - dateString.length() - 1;
        for (int i = 0; i < n; i++) {
            if (Character.isDigit(directoryName.charAt(i))) {
                return null;
            }
        }

        switch (dateString.length()) {
            case 8:
                return dayRange(dateString);
            case 6:
                return monthRange(dateString);
            case 4:
                return yearRange(dateString);
            default:
                LOGGER.debug("Cannot determine date range for directory: {}", directoryName);
                return null;
        }
    }

    // yyyymmdd
    private DateRange dayRange(String dayString) {
        int i = Integer.parseInt(dayString);
        int year = i / 10000;
        int month = (i % 10000) / 100;
        int day = i % 100;
        return DateRange.forDay(year, month, day);
    }

    // yyyymm
    private DateRange monthRange(String monthString) {
        int i = Integer.parseInt(monthString);
        int year = i / 100;
        int month = i % 100;
        return DateRange.forMonth(year, month);
    }

    // yyyy
    private DateRange yearRange(String yearString) {
        int year = Integer.parseInt(yearString);
        return DateRange.forYear(year);
    }

    /**
     * Read digits from the start of the directory name until a non-digit is
     * encountered.
     */
    private String getDateString(String directoryName) {
        // improve this.
        //        int index = 0;
        //        while (index < directoryName.length() && Character.isDigit(directoryName.charAt(index))) {
        //            index++;
        //        }
        //
        //        return directoryName.substring(0, index);

        int dirNameLength = directoryName.length();
        int index = dirNameLength - 1;
        while (index >= 0 && Character.isDigit(directoryName.charAt(index))) {
            index--;
        }

        return directoryName.substring(index + 1);
    }
}
