package uk.co.magictractor.spew.flickr;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.spew.common.filter.DateUploadedPhotoFilter;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhotos;
import uk.co.magictractor.spew.local.dates.DateRange;

/**
 * Uses flickr.photos.search rather than flickr.people.getPhotos because it
 * allows sort order to be specified. See
 * https://www.flickr.com/services/api/flickr.photos.search.html.
 */
public class FlickrPhotoIterator extends PageCountServiceIterator<FlickrPhoto> {

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

    private FlickrPhotoIterator() {
    }

    @Override
    protected List<FlickrPhoto> fetchPage(int pageNumber) {
        SpewRequest request = SpewRequest.createPostRequest(Flickr.REST_ENDPOINT);

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
        SpewResponse response = getConnection().request(request);

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

    public static class FlickrPhotoIteratorBuilder
            extends PageCountServiceIteratorBuilder<FlickrPhoto, FlickrPhotoIterator, FlickrPhotoIteratorBuilder> {

        public FlickrPhotoIteratorBuilder(SpewConnection connection) {
            super(connection, new FlickrPhotoIterator());
            addServerSideFilterHandler(DateTakenPhotoFilter.class, this::setDateTakenPhotoFilter);
            addServerSideFilterHandler(DateUploadedPhotoFilter.class, this::setDateUploadedPhotoFilter);
        }

        public void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
            getIteratorInstance().minTakenDate = convert(filter.getFrom());
            getIteratorInstance().maxTakenDate = convert(filter.getTo());
        }

        public void setDateUploadedPhotoFilter(DateUploadedPhotoFilter filter) {
            getIteratorInstance().minUploadedDate = convert(filter.getFrom());
            getIteratorInstance().maxUploadedDate = convert(filter.getTo());
        }

        private String convert(LocalDate localDate) {
            return localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
        }

    }

    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyFlickrApp.class);
        Iterator<FlickrPhoto> iter = new FlickrPhotoIteratorBuilder(connection)
                .withFilter(new DateTakenPhotoFilter(DateRange.forMonth(2019, 2)))
                .build();
        while (iter.hasNext()) {
            FlickrPhoto photo = iter.next();
            System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
        }
    }

}
