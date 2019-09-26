package uk.co.magictractor.spew.photo.filter;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

public class DateTakenPhotoFilter extends DateRangePhotoFilter {

    public DateTakenPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Media image) {
        return getDateRange().contains(image.getDateTaken());
    }

}
