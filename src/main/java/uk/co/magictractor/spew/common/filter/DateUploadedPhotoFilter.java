package uk.co.magictractor.spew.common.filter;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.local.dates.DateRange;

public class DateUploadedPhotoFilter extends DateRangePhotoFilter {

    public DateUploadedPhotoFilter(DateRange dateRange) {
        super(dateRange);
    }

    @Override
    public boolean test(Photo photo) {
        return getDateRange().contains(photo.getDateUpload());
    }

}
