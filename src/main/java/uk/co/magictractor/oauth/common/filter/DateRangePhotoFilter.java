package uk.co.magictractor.oauth.common.filter;

import java.time.LocalDate;

import uk.co.magictractor.oauth.local.dates.DateRange;

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
