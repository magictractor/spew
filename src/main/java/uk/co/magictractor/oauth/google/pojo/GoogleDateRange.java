package uk.co.magictractor.oauth.google.pojo;

import uk.co.magictractor.oauth.local.dates.DateRange;

/**
 * Used when making requests with a date range.
 * 
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
