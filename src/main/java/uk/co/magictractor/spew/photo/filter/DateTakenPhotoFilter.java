package uk.co.magictractor.spew.photo.filter;

import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

public class DateTakenPhotoFilter extends DateRangePhotoFilter {

    public DateTakenPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Photo photo) {
        return getDateRange().contains(photo.getDateTaken());
    }

}
