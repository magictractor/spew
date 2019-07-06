package uk.co.magictractor.spew.google.pojo;

import uk.co.magictractor.spew.local.dates.DateRange;

/**
 * Currently only a single date range filter is supported.
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#Filters
 */
public class GoogleFilters {

    private GoogleDateFilter dateFilter;

    public GoogleFilters(DateRange dateRange) {
        dateFilter = new GoogleDateFilter(dateRange);
    }
}
