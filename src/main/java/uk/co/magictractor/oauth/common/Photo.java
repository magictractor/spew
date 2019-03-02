package uk.co.magictractor.oauth.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface Photo {

	// file name
	String getFileName();

	String getTitle();

	String getDescription();

	// we (generally?) don't know the time zone
	LocalDateTime getDateTimeTaken();

	default LocalDate getDateTaken() {
		return getDateTimeTaken().toLocalDate();
	}

	default LocalTime getTimeTaken() {
		return getDateTimeTaken().toLocalTime();
	}

	String getShutterSpeed();

	String getAperture();

	int getIso();

	TagSet getTagSet();

	// rating

	// dimensions

	// one day - on demand get hold of image?
}
