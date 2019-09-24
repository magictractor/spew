package uk.co.magictractor.spew.provider.google;

import com.google.common.base.MoreObjects;

import uk.co.magictractor.spew.photo.local.dates.DateRange;

/**
 * Currently only a single date range filter is supported.
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#Filters
 */
public class GoogleFilters {

    private GoogleDateFilter dateFilter;

    public GoogleFilters(DateRange dateRange) {
        dateFilter = new GoogleDateFilter(dateRange);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("dateFilter", dateFilter)
                .toString();
    }

}
