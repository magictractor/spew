package uk.co.magictractor.oauth.google;

import java.util.List;

import uk.co.magictractor.oauth.api.OAuth2Connection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageTokenServiceIterator;

public abstract class GoogleServiceIterator<E> extends PageTokenServiceIterator<E> {

	@Override
	protected final List<? extends E> fetchPage(String pageToken) {
		OAuthRequest request = createPageRequest();

		request.setParam("pageToken", pageToken);

		OAuthResponse response = new OAuth2Connection(new MyGooglePhotosApp()).request(request);

		System.err.println(response);

		setNextPageToken(response.getString("nextPageToken"));

		return parsePageResponse(response);
	}
	
	protected abstract OAuthRequest createPageRequest();
	
	protected abstract List<? extends E> parsePageResponse(OAuthResponse response);
	
}
