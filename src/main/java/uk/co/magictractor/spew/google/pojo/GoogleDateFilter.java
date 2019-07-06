package uk.co.magictractor.spew.google.pojo;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.local.dates.DateRange;

/**
 * Currently only a single date range is supported, but Google can handle
 * multiple dates and ranges.
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#DateFilter
 */
public class GoogleDateFilter {

    private List<GoogleDateRange> ranges;

    public GoogleDateFilter(DateRange dateRange) {
        ranges = Arrays.asList(new GoogleDateRange(dateRange));
    }
}