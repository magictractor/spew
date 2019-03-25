package uk.co.magictractor.oauth.common.filter;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.local.dates.DateRange;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class DateUploadedPhotoFilter extends DateRangePhotoFilter {

	public DateUploadedPhotoFilter(DateRange dateRange) {
		super(dateRange);
	}

	@Override
	public boolean test(Photo photo) {
		return getDateRange().contains(photo.getDateUpload());
	}

}
