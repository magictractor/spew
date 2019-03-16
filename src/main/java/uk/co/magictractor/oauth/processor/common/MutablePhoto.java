package uk.co.magictractor.oauth.processor.common;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

// TODO! this could/should implement Photo?
public class MutablePhoto {

	// private final Photo photo;
	private final String photoId;
	private final String originalTitle;
	// TODO! private or bin
	public final LocalDate originalDateTaken;
	private final Instant originalDateTimeUpload;
	private final TagSet originalTagSet;

	private String title;
	private Instant dateTimeUpload;
	private TagSet tagSet;

	public MutablePhoto(Photo photo) {
		photoId = photo.getServiceProviderId();
		originalTitle = photo.getTitle();
		originalDateTaken = photo.getDateTaken();
		originalDateTimeUpload = photo.getDateTimeUpload();
		originalTagSet = photo.getTagSet();

		title = originalTitle;
		dateTimeUpload = originalDateTimeUpload;
		tagSet = new TagSet(originalTagSet);
	}
	
	public String getServiceProviderId() {
		return photoId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isTitleChanged() {
		return !Objects.equals(originalTitle, title);
	}
	
	public boolean isTagSetChanged() {
		return !Objects.equals(originalTagSet, tagSet);
	}

	// No setter, add and remove Tag instances this Tags instance
	public TagSet getTagSet() {
		return tagSet;
	}

}
