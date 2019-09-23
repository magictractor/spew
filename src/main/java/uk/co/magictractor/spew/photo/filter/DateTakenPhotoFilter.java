package uk.co.magictractor.spew.photo.filter;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

public class DateTakenPhotoFilter extends DateRangePhotoFilter {

    public DateTakenPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Image image) {
        return getDateRange().contains(image.getDateTaken());
    }

}
