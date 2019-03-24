package uk.co.magictractor.oauth.common;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

public interface Photo {

	/**
	 * The id used by the service provider (Flickr etc) to identify the photograph.
	 * 
	 * This is used when making API calls which modify the image properties.
	 */
	String getServiceProviderId();

	// file name
	String getFileName();

	String getTitle();

	String getDescription();

	TagSet getTagSet();

	Instant getDateTimeTaken();

	default Integer getRating() {
		return null;
	}

	default LocalDate getDateTaken() {
		// return getDateTimeTaken().toLocalDate();
		return getDateTimeTaken() == null ? null
				: LocalDateTime.ofInstant(getDateTimeTaken(), ZoneId.systemDefault()).toLocalDate();
	}

	default LocalTime getTimeTaken() {
		// return getDateTimeTaken().toLocalTime();
		// return getDateTimeTaken() == null ? null :
		// LocalTime.from(getDateTimeTaken());
		return getDateTimeTaken() == null ? null
				: LocalDateTime.ofInstant(getDateTimeTaken(), ZoneId.systemDefault()).toLocalTime();
	}

	Instant getDateTimeUpload();

	default LocalDate getDateUpload() {
		// return getDateTimeUpload().toLocalDate();
		// return getDateTimeUpload() == null ? null :
		// LocalDate.from(getDateTimeUpload());
		return getDateTimeUpload() == null ? null
				: LocalDateTime.ofInstant(getDateTimeUpload(), ZoneId.systemDefault()).toLocalDate();
	}

	default LocalTime getTimeUpload() {
		// return getDateTimeUpload().toLocalTime();
		// return getDateTimeUpload() == null ? null :
		// LocalTime.from(getDateTimeUpload());
		return getDateTimeUpload() == null ? null
				: LocalDateTime.ofInstant(getDateTimeUpload(), ZoneId.systemDefault()).toLocalTime();
	}

	default String getShutterSpeed() {
		return null;
	}

	default String getAperture() {
		return null;
	}

	default Integer getIso() {
		return null;
	}

	// exposure comp

	// focal length (and ff equiv?)

	Integer getWidth();

	Integer getHeight();

	public static ToStringHelper toStringHelper(Photo photo) {
		ToStringHelper toStringHelper = MoreObjects.toStringHelper(photo);
		if (photo.getServiceProviderId() != null) {
			toStringHelper.add("serviceProviderId", photo.getServiceProviderId());
		}
		if (photo.getFileName() != null) {
			toStringHelper.add("fileName", photo.getFileName());
		}
		return toStringHelper;
	}

	// one day - on demand get hold of image? - will need for uploading local photo
	// to service provider

}
