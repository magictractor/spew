package uk.co.magictractor.spew.provider.google;

import java.time.LocalDate;

import com.google.common.base.MoreObjects;

/**
 * Used when making requests with a date range.
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#Date
 */
public class GoogleDate {

    private int year;
    private int month;
    private int day;

    public GoogleDate(LocalDate localDate) {
        this.year = localDate.getYear();
        this.month = localDate.getMonthValue();
        this.day = localDate.getDayOfMonth();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("year", year)
                .add("month", month)
                .add("day", day)
                .toString();
    }

}
