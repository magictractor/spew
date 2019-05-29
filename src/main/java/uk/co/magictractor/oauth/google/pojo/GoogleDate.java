package uk.co.magictractor.oauth.google.pojo;

import java.time.LocalDate;

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

}
