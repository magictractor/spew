package uk.co.magictractor.spew.photo.filter;

import java.time.LocalDate;

import uk.co.magictractor.spew.photo.local.dates.DateRange;

public abstract class DateRangePhotoFilter implements PhotoFilter {

    private final DateRange dateRange;

    protected DateRangePhotoFilter(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public LocalDate getFrom() {
        return dateRange.getFrom();
    }

    public LocalDate getTo() {
        return dateRange.getTo();
    }

}
