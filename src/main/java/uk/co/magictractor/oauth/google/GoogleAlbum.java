package uk.co.magictractor.oauth.google;

import uk.co.magictractor.oauth.common.Album;

// https://developers.google.com/photos/library/reference/rest/v1/albums#Album
public class GoogleAlbum implements Album {

	private String id;
	// TODO! private??
	private String title;
	// meh - this is not the same as the share link - can the API fetch a share link?
	private String productUrl;
	private int mediaItemsCount;
	private String coverPhotoBaseUrl;
	private String coverPhotoMediaItemId;
	// TODO! shareInfo

	public String getId() {
		return id;
	}

	// TODO! Lombok?
	public String getTitle() {
		return title;
	}

	@Override
	public String getAlbumUrl() {
		return productUrl;
	}

	@Override
	public String getCoverPhotoBaseUrl() {
		return coverPhotoBaseUrl;
	}

	@Override
	public int getPhotoCount() {
		// TODO! might not all be photos (media item includes video too)
		return mediaItemsCount;
	}

}
