package uk.co.magictractor.oauth.local.dates;

public class DefaultLocalDirectoryDatesStrategy implements LocalDirectoryDatesStrategy {

    // 4 digits exactly assumed to be year, such as 2019
    // 6 digits assumed to be year and zero padded month such as 2019
    // 8 digits assumed to be year, zero passed month and zero padded day, such as
    // 20190123
    // hmm - 2018 has files like "190_1712" and "190_1812_rgbe" - perhaps rename
    // these??
    @Override
    public DateRange getDateRange(String directoryName) {
        String dateString = getDateString(directoryName);
        switch (dateString.length()) {
            case 8:
                return dayRange(dateString);
            case 6:
                return monthRange(dateString);
            case 4:
                return yearRange(dateString);
            default:
                System.err.println("Cannot determine date range for directory: " + directoryName);
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
        int index = 0;
        while (index < directoryName.length() && Character.isDigit(directoryName.charAt(index))) {
            index++;
        }

        return directoryName.substring(0, index);
    }
}
