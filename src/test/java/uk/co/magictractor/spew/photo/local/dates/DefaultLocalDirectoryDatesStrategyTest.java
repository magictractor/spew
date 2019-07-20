package uk.co.magictractor.spew.photo.local.dates;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.local.dates.DateRange;
import uk.co.magictractor.spew.photo.local.dates.DefaultLocalDirectoryDatesStrategy;

public class DefaultLocalDirectoryDatesStrategyTest {

    private DefaultLocalDirectoryDatesStrategy strategy = new DefaultLocalDirectoryDatesStrategy();

    @Test
    public void testYear() {
        DateRange actual = strategy.getDateRange("2018");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2018, 1, 1));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2018, 12, 31));
    }

    @Test
    public void test31DayMonth() {
        DateRange actual = strategy.getDateRange("201801");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2018, 1, 1));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2018, 1, 31));
    }

    @Test
    public void test30DayMonth() {
        DateRange actual = strategy.getDateRange("201806");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2018, 6, 1));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2018, 6, 30));
    }

    @Test
    public void test29DayMonth() {
        DateRange actual = strategy.getDateRange("201602");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2016, 2, 1));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2016, 2, 29));
    }

    @Test
    public void test28DayMonth() {
        DateRange actual = strategy.getDateRange("201802");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2018, 2, 1));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2018, 2, 28));
    }

    @Test
    public void testDay() {
        DateRange actual = strategy.getDateRange("20180212");
        assertThat(actual.getFrom()).isEqualTo(LocalDate.of(2018, 2, 12));
        assertThat(actual.getTo()).isEqualTo(LocalDate.of(2018, 2, 12));
    }

    @Test
    public void testOtherDigits() {
        DateRange actual = strategy.getDateRange("123_0408");
        assertThat(actual).isNull();
    }

    @Test
    public void testNoDigits() {
        DateRange actual = strategy.getDateRange("Backup");
        assertThat(actual).isNull();
    }

    @Test
    public void testNoDigitsAndShort() {
        DateRange actual = strategy.getDateRange("X");
        assertThat(actual).isNull();
    }

}
