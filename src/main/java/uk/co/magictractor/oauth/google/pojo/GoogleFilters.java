package uk.co.magictractor.oauth.google.pojo;

import uk.co.magictractor.oauth.local.dates.DateRange;

/**
 * Currently only a single date range filter is supported.
 * 
 * https://developers.google.com/photos/library/reference/rest/v1/mediaItems/search#Filters
 */
public class GoogleFilters {

	private GoogleDateFilter dateFilter;

	public GoogleFilters(DateRange dateRange) {
		dateFilter = new GoogleDateFilter(dateRange);
	}
}
