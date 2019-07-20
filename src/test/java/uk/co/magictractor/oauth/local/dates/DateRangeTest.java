package uk.co.magictractor.oauth.local.dates;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.local.dates.DateRange;

public class DateRangeTest {

    private LocalDate someDate() {
        return LocalDate.of(2018, 5, 20);
    }

    @Test
    public void testConstructorSameDay() {
        LocalDate date = someDate();
        DateRange range = new DateRange(date, date);

        assertThat(range.getFrom()).isEqualTo(date);
        assertThat(range.getTo()).isEqualTo(date);
    }

    @Test
    public void testConstructorDifferentDays() {
        LocalDate date1 = someDate();
        LocalDate date2 = date1.plusDays(1);
        DateRange range = new DateRange(date1, date2);

        assertThat(range.getFrom()).isEqualTo(date1);
        assertThat(range.getTo()).isEqualTo(date2);
    }

    @Test
    public void testConstructorNullFrom() {
        assertThatThrownBy(() -> new DateRange(null, someDate())).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("from must not be null");
    }

    @Test
    public void testConstructorNullTo() {
        assertThatThrownBy(() -> new DateRange(someDate(), null)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("to must not be null");
    }

    @Test
    public void testConstructorFromAfterTo() {
        LocalDate date1 = someDate();
        LocalDate date2 = date1.minusDays(1);

        assertThatThrownBy(() -> new DateRange(date1, date2)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("to must not be before from");
    }

    @Test
    public void testContainsFromDate() {
        DateRange april = DateRange.forMonth(2018, 4);
        assertThat(april.contains(april.getFrom())).isTrue();
    }

    @Test
    public void testContainsToDate() {
        DateRange april = DateRange.forMonth(2018, 4);
        assertThat(april.contains(april.getTo())).isTrue();
    }

    @Test
    public void testContainsMidDate() {
        DateRange april = DateRange.forMonth(2018, 4);
        assertThat(april.contains(april.getFrom().plusDays(10))).isTrue();
    }

    @Test
    public void testContainsJustBeforeFrom() {
        DateRange april = DateRange.forMonth(2018, 4);
        assertThat(april.contains(april.getFrom().minusDays(1))).isFalse();
    }

    @Test
    public void testContainsJustAfterTo() {
        DateRange april = DateRange.forMonth(2018, 4);
        assertThat(april.contains(april.getTo().plusDays(1))).isFalse();
    }

    @Test
    public void testIntersectAdjacent() {
        DateRange april = DateRange.forMonth(2018, 4);
        DateRange may = DateRange.forMonth(2018, 5);

        assertThat(april.intersects(may)).isFalse();
        assertThat(may.intersects(april)).isFalse();
    }

    @Test
    public void testIntersectWhollyWithin() {
        DateRange year = DateRange.forYear(2018);
        DateRange month = DateRange.forMonth(2018, 5);

        assertThat(year.intersects(month)).isTrue();
        assertThat(month.intersects(year)).isTrue();
    }

    @Test
    public void testIntersectOverlap() {
        DateRange aprilMay = new DateRange(LocalDate.of(2018, 4, 1), LocalDate.of(2018, 5, 31));
        DateRange mayJune = new DateRange(LocalDate.of(2018, 5, 1), LocalDate.of(2018, 6, 30));

        assertThat(aprilMay.intersects(mayJune)).isTrue();
        assertThat(mayJune.intersects(aprilMay)).isTrue();
    }

    @Test
    public void testIntersectOverlapByOneDay() {
        DateRange first = new DateRange(LocalDate.of(2018, 4, 1), LocalDate.of(2018, 5, 31));
        DateRange second = new DateRange(LocalDate.of(2018, 5, 31), LocalDate.of(2018, 6, 30));

        assertThat(first.intersects(second)).isTrue();
        assertThat(second.intersects(first)).isTrue();
    }
}
