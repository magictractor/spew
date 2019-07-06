package uk.co.magictractor.spew.common.filter;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.local.dates.DateRange;

public class DateTakenPhotoFilter extends DateRangePhotoFilter {

    public DateTakenPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Photo photo) {
        return getDateRange().contains(photo.getDateTaken());
    }

}
