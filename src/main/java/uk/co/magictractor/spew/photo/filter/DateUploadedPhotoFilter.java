package uk.co.magictractor.spew.photo.filter;

import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

public class DateUploadedPhotoFilter extends DateRangePhotoFilter {

    public DateUploadedPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Photo photo) {
        return getDateRange().contains(photo.getDateUpload());
    }

}
