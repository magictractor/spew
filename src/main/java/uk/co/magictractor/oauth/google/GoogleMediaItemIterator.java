package uk.co.magictractor.oauth.google;

import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<GoogleMediaItem> {

	@Override
	protected OAuthRequest createPageRequest() {
		return new OAuthRequest("https://photoslibrary.googleapis.com/v1/mediaItems");
	}

	@Override
	protected List<GoogleMediaItem> parsePageResponse(OAuthResponse response) {
		return response.getObject("mediaItems", new TypeRef<List<GoogleMediaItem>>() {
		});
	}

	public static void main(String[] args) {
		GoogleMediaItemIterator iter = new GoogleMediaItemIterator();
		while (iter.hasNext()) {
			GoogleMediaItem photo = iter.next();
			System.err.println(photo.getFileName() + "  " + photo.getDateTimeTaken());
		}
	}

}
