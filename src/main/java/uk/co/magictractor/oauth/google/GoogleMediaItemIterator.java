package uk.co.magictractor.oauth.google;

import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuth2Connection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<MediaItem> {

	// min_taken_date (Optional)
	// Minimum taken date. Photos with an taken date greater than or equal to this
	// value will be returned. The date can be in the form of a mysql datetime or
	// unix timestamp.

	// Get images https://apidocs.imgur.com/#2e45daca-bd44-47f8-84b0-b3f2aa861735
	@Override
	protected List<MediaItem> fetchPage(String pageToken) {
		// https://photoslibrary.googleapis.com/v1/mediaItems
		// OAuthRequest request = new OAuthRequest(Google.REST_ENDPOINT);
		OAuthRequest request = new OAuthRequest("https://photoslibrary.googleapis.com/v1/mediaItems");

		request.setParam("pageToken", pageToken);

//		request.setParam("method", "flickr.photos.search");
//
//		request.setParam("user_id", "me");
//		request.setParam("sort", "date-taken-desc");
//		request.setParam("page", pageNumber);
//		// default is 100, max is 500
//		request.setParam("per_page", 500);

		// request.setParam("min_taken_date", "2018-10-10");
		// request.setParam("min_taken_date", "2018-10-06");

		// machine_tags are no auto tags
		// https://www.flickr.com/groups/51035612836@N01/discuss/72157594497877875/
		// request.setParam("extras",
		// "date_upload,date_taken,description,tags,machine_tags");
		// request.setParam("extras", ALL_EXTRAS);
		OAuthResponse response = new OAuth2Connection(new Google()).request(request);

		// https://stackoverflow.com/questions/13686284/parsing-jsonobject-to-listmapstring-object-using-gson

		System.err.println(response);

		// works, but want counts too
//		TypeRef<List<Photo>> type = new TypeRef<List<Photo>>() {
//		};
//		List<Photo> photos = response.getObject("photos.photo", type);
		// photo -> ArrayList
		// return new ArrayList<Photo>(photos.values());

//		Photos photos = response.getObject("photos", Photos.class);
//		setTotalItemCount(photos.total);
//		setTotalPageCount(photos.pages);
//
//		return photos.photo;

		setNextPageToken(response.getString("nextPageToken"));

		return response.getObject("mediaItems", new TypeRef<List<MediaItem>>() {
		});
	}

	public static void main(String[] args) {
		GoogleMediaItemIterator iter = new GoogleMediaItemIterator();
		while (iter.hasNext()) {
			System.err.println(iter.next().filename);
		}
	}
}
