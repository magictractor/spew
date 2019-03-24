package uk.co.magictractor.oauth.local.dates;

import java.time.LocalDate;

public class DateRange {

	private LocalDate from;
	private LocalDate to;

	public DateRange(LocalDate from, LocalDate to) {
		if (from == null) {
			throw new IllegalArgumentException("from must not be null");
		}
		if (to == null) {
			throw new IllegalArgumentException("to must not be null");
		}
		if (to.isBefore(from)) {
			throw new IllegalArgumentException("to must not be before from");
		}

		this.from = from;
		this.to = to;
	}

	public LocalDate getFrom() {
		return from;
	}

	public LocalDate getTo() {
		return to;
	}

	public boolean contains(LocalDate date) {
		return !date.isBefore(from) && !date.isAfter(to);
	}

	public boolean intersects(DateRange other) {
		return !other.to.isBefore(from) && !other.from.isAfter(to);
	}
	
	public String toString() {
		return "DateRange[" + from + " to " + to + "]";
	}

	public static DateRange uptoToday(int daysBefore) {
		// TODO! pass a clock to now for unit testing
		LocalDate to = LocalDate.now();
		LocalDate from = to.minusDays(0);
		return new DateRange(from, to);
	}
	
	public static DateRange forDay(int year, int month, int day) {
		LocalDate from = LocalDate.of(year, month, day);
		return new DateRange(from, from);
	}
	
	public static DateRange thisMonth() {
		// TODO! clock
		LocalDate from = LocalDate.now().withDayOfMonth(1);
		LocalDate to = from.plusMonths(1).minusDays(1);
		return new DateRange(from, to);
	}

	public static DateRange forMonth(int year, int month) {
		LocalDate from = LocalDate.of(year, month, 1);
		LocalDate to = from.plusMonths(1).minusDays(1);
		return new DateRange(from, to);
	}

	public static DateRange forYear(int year) {
		LocalDate from = LocalDate.of(year, 1, 1);
		LocalDate to = from.plusYears(1).minusDays(1);
		return new DateRange(from, to);
	}

}
