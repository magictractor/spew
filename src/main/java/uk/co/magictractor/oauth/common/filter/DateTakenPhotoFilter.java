package uk.co.magictractor.oauth.common.filter;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.local.dates.DateRange;

public class DateTakenPhotoFilter extends DateRangePhotoFilter {

    public DateTakenPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Photo photo) {
        return getDateRange().contains(photo.getDateTaken());
    }

}
