package uk.co.magictractor.oauth.flickr;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import uk.co.magictractor.oauth.api.OAuth1Application;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageCountServiceIterator;
import uk.co.magictractor.oauth.api.PhotoIterator;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.common.filter.DateUploadedPhotoFilter;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhotos;
import uk.co.magictractor.oauth.local.dates.DateRange;

/**
 * Uses flickr.photos.search rather than flickr.people.getPhotos because it
 * allows sort order to be specified.
 * 
 * See https://www.flickr.com/services/api/flickr.photos.search.html.
 */
public class FlickrPhotoIterator extends PageCountServiceIterator<FlickrPhoto> implements PhotoIterator<FlickrPhoto> {

	private static final List<Class<? extends PhotoFilter>> SUPPORTED_FILTERS = Arrays
			.asList(DateTakenPhotoFilter.class, DateUploadedPhotoFilter.class);

	// From API doc: "The date can be in the form of a unix timestamp or mysql
	// datetime."
	private String minTakenDate;
	private String maxTakenDate;
	private String minUploadedDate;
	private String maxUploadedDate;

	// min_taken_date (Optional)
	// Minimum taken date. Photos with an taken date greater than or equal to this
	// value will be returned. The date can be in the form of a mysql datetime or
	// unix timestamp.

	// hmm, not convinced about generics on OAuthApplication
	public FlickrPhotoIterator(OAuth1Application application) {
		super(application);
	}

	@Override
	public Collection<Class<? extends PhotoFilter>> supportedPhotoFilters() {
		return SUPPORTED_FILTERS;
	}

	@Override
	protected List<FlickrPhoto> fetchPage(int pageNumber) {
		OAuthRequest request = OAuthRequest.createPostRequest(Flickr.REST_ENDPOINT);

		request.setParam("method", "flickr.photos.search");

		request.setParam("user_id", "me");
		request.setParam("sort", "date-taken-desc");
		request.setParam("page", pageNumber);
		// default is 100, max is 500
		// request.setParam("per_page", 500);

		// Filters
		request.setParam("min_taken_date", minTakenDate);
		request.setParam("max_taken_date", maxTakenDate);
		request.setParam("min_upload_date", minUploadedDate);
		request.setParam("max_upload_date", maxUploadedDate);

		// machine_tags are no auto tags
		// https://www.flickr.com/groups/51035612836@N01/discuss/72157594497877875/
		//
		// url_o is a workaround for o_dims not working
		// https://www.flickr.com/groups/51035612836@N01/discuss/72157649995435595/
		request.setParam("extras", "date_upload,date_taken,description,tags,machine_tags,url_o");
		// request.setParam("extras", ALL_EXTRAS);
		OAuthResponse response = getConnection().request(request);

		// https://stackoverflow.com/questions/13686284/parsing-jsonobject-to-listmapstring-object-using-gson

		System.err.println(response);

		// works, but want counts too
//		TypeRef<List<Photo>> type = new TypeRef<List<Photo>>() {
//		};
//		List<Photo> photos = response.getObject("photos.photo", type);
		// photo -> ArrayList
		// return new ArrayList<Photo>(photos.values());

		FlickrPhotos photos = response.getObject("photos", FlickrPhotos.class);
		setTotalItemCount(photos.total);
		setTotalPageCount(photos.pages);

		return photos.photo;
	}

	@Override
	public void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
		minTakenDate = convert(filter.getFrom());
		maxTakenDate = convert(filter.getTo());
	}

	@Override
	public void setDateUploadedPhotoFilter(DateUploadedPhotoFilter filter) {
		minUploadedDate = convert(filter.getFrom());
		maxUploadedDate = convert(filter.getTo());
	}

	private String convert(LocalDate localDate) {
		return localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
	}

	public static void main(String[] args) {
		FlickrPhotoIterator iter = new FlickrPhotoIterator(MyFlickrApp.getInstance());
		iter.addFilter(new DateTakenPhotoFilter(DateRange.forMonth(2019, 2)));
		// iter.addFilter(new DateUploadedPhotoFilter(DateRange.forMonth(2019, 3)));
		while (iter.hasNext()) {
			FlickrPhoto photo = iter.next();
			System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
		}
	}

}
