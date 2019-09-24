package uk.co.magictractor.spew.provider.google;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.MoreObjects;

import uk.co.magictractor.spew.photo.local.dates.DateRange;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ranges", ranges)
                .toString();
    }

}
