package uk.co.magictractor.oauth.flickr.processor;

import java.time.Instant;

import uk.co.magictractor.oauth.api.OAuth1Connection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.flickr.Flickr;
import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.processor.Changes;

public class FlickrPhotoChanges implements Changes<Photo> {

	// private final Photo photo;
	private final String photoId;
	private final String originalTitle;
	private final Instant originalDateUpload;
	private final TagSet originalTagSet;

	private String title;
	private Instant dateUpload;
	private TagSet tagSet;

	public FlickrPhotoChanges(Photo photo) {
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

		if (!originalTitle.equals(title)) {
			setMeta();
		}

		if (!originalTagSet.equals(tagSet)) {
			setTags();
		}
	}

//	api_key (Required)
//	Your API application key. See here for more details.
//	photo_id (Required)
//	The id of the photo to set information for.
//	title (Optional)
//	The title for the photo. At least one of title or description must be set.
//	description (Optional)
//	The description for the photo. At least one of title or description must be set.
	private void setMeta() {
		// TODO Auto-generated method stub
		System.err.println("set title: " + title);

		OAuthRequest request = new OAuthRequest(Flickr.REST_ENDPOINT);

		request.setHttpMethod("POST");
		request.setParam("method", "flickr.photos.setMeta");

		request.setParam("photo_id", photoId);
		request.setParam("title", title);

		//
		OAuthResponse response = new OAuth1Connection(new Flickr()).request(request);
	}

	private void setTags() {
		// TODO Auto-generated method stub
		System.err.println("set tags: " + tagSet);

		OAuthRequest request = new OAuthRequest(Flickr.REST_ENDPOINT);

		request.setHttpMethod("POST");
		request.setParam("method", "flickr.photos.setTags");

		request.setParam("photo_id", photoId);
		// TODO! refactor this method - a processor has added the parents already
		request.setParam("tags", tagSet.getCompactTagNamesWithParents());

		OAuthResponse response = new OAuth1Connection(new Flickr()).request(request);
	}

	// App Garden
//	{ "photo": { 
//	    "title": { "_content": "API test" }, 
//	    "description": { "_content": "" } }, "stat": "ok" }

	// https://api.flickr.com/services/rest/?method=flickr.photos.setMeta&api_key=5939e168bc6ea2e41e83b74f6f0b3e2d&photo_id=45249983521&title=API+test&format=json&nojsoncallback=1&auth_token=72157672277056577-9fa9087d61430e0a&api_sig=50453767437b384152449e7cb561ac02

}

//set title: Small white
//<?xml version="1.0" encoding="utf-8" ?>
//<rsp stat="fail">
//	<err code="100" msg="Invalid API Key (Key has invalid format)" />
//</rsp>
