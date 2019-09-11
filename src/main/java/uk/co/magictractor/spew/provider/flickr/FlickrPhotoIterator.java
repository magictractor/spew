package uk.co.magictractor.spew.provider.flickr;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.photo.filter.DateTakenPhotoFilter;
import uk.co.magictractor.spew.photo.filter.DateUploadedPhotoFilter;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

/**
 * Uses flickr.photos.search rather than flickr.people.getPhotos because it
 * allows sort order to be specified. See
 * https://www.flickr.com/services/api/flickr.photos.search.html.
 */
public class FlickrPhotoIterator<E> extends PageCountServiceIterator<E> {

    // Default so other iterators can use the same constant
    /* default */ static final String EXTRAS = "date_upload,date_taken,description,tags,machine_tags,url_o";

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
    protected List<E> fetchPage(int pageNumber) {
        OutgoingHttpRequest request = createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photos.search");

        request.setQueryStringParam("user_id", "me");
        request.setQueryStringParam("sort", "date-taken-desc");
        request.setQueryStringParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        // Filters
        request.setQueryStringParam("min_taken_date", minTakenDate);
        request.setQueryStringParam("max_taken_date", maxTakenDate);
        request.setQueryStringParam("min_upload_date", minUploadedDate);
        request.setQueryStringParam("max_upload_date", maxUploadedDate);

        // machine_tags are no auto tags
        // https://www.flickr.com/groups/51035612836@N01/discuss/72157594497877875/
        //
        // url_o is a workaround for o_dims not working
        // https://www.flickr.com/groups/51035612836@N01/discuss/72157649995435595/
        request.setQueryStringParam("extras", EXTRAS);
        // request.setParam("extras", ALL_EXTRAS);
        SpewParsedResponse response = request.sendRequest();

        // https://stackoverflow.com/questions/13686284/parsing-jsonobject-to-listmapstring-object-using-gson

        System.err.println(response);

        setTotalItemCount(response.getInt("$.photos.total"));
        setTotalPageCount(response.getInt("$.photos.pages"));

        return response.getList("$.photos.photo", getElementType());
    }

    public static class FlickrPhotoIteratorBuilder<E>
            extends PageCountServiceIteratorBuilder<E, FlickrPhotoIterator<E>, FlickrPhotoIteratorBuilder<E>> {

        public FlickrPhotoIteratorBuilder(SpewApplication<Flickr> application, Class<E> elementType) {
            super(application, elementType, new FlickrPhotoIterator<>());
            addServerSideFilterHandler(DateTakenPhotoFilter.class, this::setDateTakenPhotoFilter);
            addServerSideFilterHandler(DateUploadedPhotoFilter.class, this::setDateUploadedPhotoFilter);
        }

        public void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
            getIteratorInstance().minTakenDate = convert(filter.getFrom());
            // TODO one second out - below too
            getIteratorInstance().maxTakenDate = convert(filter.getTo().plusDays(1));
        }

        public void setDateUploadedPhotoFilter(DateUploadedPhotoFilter filter) {
            getIteratorInstance().minUploadedDate = convert(filter.getFrom());
            getIteratorInstance().maxUploadedDate = convert(filter.getTo().plusDays(1));

            // super.build();
        }

        private String convert(LocalDate localDate) {
            return localDate.getYear() + "-" + localDate.getMonthValue() + "-" + localDate.getDayOfMonth();
        }

    }

    public static void main(String[] args) {
        Iterator<FlickrPhoto> iter = new FlickrPhotoIteratorBuilder<>(new MyFlickrApp(), FlickrPhoto.class)
                .withFilter(new DateTakenPhotoFilter(DateRange.forMonth(2019, 2)))
                .build();
        while (iter.hasNext()) {
            FlickrPhoto photo = iter.next();
            System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
        }
    }

}
