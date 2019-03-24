package uk.co.magictractor.oauth.google;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<GoogleMediaItem> {

	private static final List<Class<? extends PhotoFilter>> SUPPORTED_FILTERS = Arrays
			.asList(DateTakenPhotoFilter.class);

	private String minTakenDate;
	private String maxTakenDate;

	@Override
	public Collection<Class<? extends PhotoFilter>> supportedPhotoFilters() {
		return SUPPORTED_FILTERS;
	}

	@Override
	protected void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
		minTakenDate = convert(filter.getFrom());
		maxTakenDate = convert(filter.getTo());
	}

	private String convert(LocalDate localDate) {
		return localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
	}

	@Override
	protected OAuthRequest createPageRequest() {
//		String endpoint = "https://photoslibrary.googleapis.com/v1/mediaItems:search";
		String endpoint = "https://photoslibrary.googleapis.com/v1/mediaItems";
		OAuthRequest request = new OAuthRequest(endpoint);

		return request;
	}

	@Override
	protected List<GoogleMediaItem> parsePageResponse(OAuthResponse response) {
		return response.getObject("mediaItems", new TypeRef<List<GoogleMediaItem>>() {
		});
	}

	public static void main(String[] args) {
		GoogleMediaItemIterator iter = new GoogleMediaItemIterator();
		// iter.addFilter(new DateTakenPhotoFilter(DateRange.forYear(2018)));
		while (iter.hasNext()) {
			GoogleMediaItem photo = iter.next();
			System.err.println(photo.getFileName() + "  " + photo.getDateTimeTaken());
		}
	}

}
