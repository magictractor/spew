package uk.co.magictractor.oauth.imgur;

import java.time.Instant;

import uk.co.magictractor.oauth.api.OAuth1Connection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.flickr.Flickr;
import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.processor.Changes;

public class ImgurPhotoChanges implements Changes<Photo> {

	// private final Photo photo;
	private final String photoId;
	private final String originalTitle;
	private final Instant originalDateUpload;
	private final TagSet originalTagSet;

	private String title;
	private Instant dateUpload;
	private TagSet tagSet;

	public ImgurPhotoChanges(Photo photo) {
		photoId = photo.id;
		originalTitle = photo.title;
		originalDateUpload = photo.dateUpload;
		originalTagSet = photo.tags;

		title = originalTitle;
		dateUpload = originalDateUpload;
		tagSet = new TagSet(originalTagSet);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// No setter, add and remove Tag instances this Tags instance
	public TagSet getTagSet() {
		return tagSet;
	}

	// title and description changed using flickr.photos.setMeta
	// tags using flickr.photos.setTags (also addTags and removeTag)
	// date posted using flickr.photos.setDates
	@Override
	public void persist() {
		System.err.println("persist: " + photoId);

//		if (!originalTitle.equals(title)) {
//			setMeta();
//		}
//
//		if (!originalTagSet.equals(tagSet)) {
//			setTags();
//		}
	}

}
