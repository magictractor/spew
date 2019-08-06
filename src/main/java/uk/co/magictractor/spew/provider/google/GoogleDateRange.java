package uk.co.magictractor.spew.provider.google;

import uk.co.magictractor.spew.photo.local.dates.DateRange;

/**
 * Used when making requests with a date range.
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#DateRange
 */
public class GoogleDateRange {

    private GoogleDate startDate;
    private GoogleDate endDate;

    public GoogleDateRange(DateRange dateRange) {
        this.startDate = new GoogleDate(dateRange.getFrom());
        this.endDate = new GoogleDate(dateRange.getTo());
    }

}
