package uk.co.magictractor.spew.google;

import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.spew.google.pojo.GoogleFilters;
import uk.co.magictractor.spew.google.pojo.GoogleMediaItem;
import uk.co.magictractor.spew.local.dates.DateRange;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<GoogleMediaItem> {

    private DateRange dateTakenRange;

    private GoogleMediaItemIterator() {
    }

    @Override
    protected SpewRequest createPageRequest() {
        SpewRequest request = SpewRequest
                .createPostRequest("https://photoslibrary.googleapis.com/v1/mediaItems:search");

        if (dateTakenRange != null) {
            request.setParam("filters", new GoogleFilters(dateTakenRange));
        }

        return request;
    }

    @Override
    protected List<GoogleMediaItem> parsePageResponse(SpewResponse response) {
        return response.getObject("mediaItems", new TypeRef<List<GoogleMediaItem>>() {
        });
    }

    public static class GoogleMediaItemIteratorBuilder extends
            GoogleServiceIteratorBuilder<GoogleMediaItem, GoogleMediaItemIterator, GoogleMediaItemIteratorBuilder> {

        public GoogleMediaItemIteratorBuilder(SpewConnection connection) {
            super(connection, new GoogleMediaItemIterator());
            addServerSideFilterHandler(DateTakenPhotoFilter.class, this::setDateTakenPhotoFilter);
        }

        private void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
            getIteratorInstance().dateTakenRange = filter.getDateRange();
        }

    }

    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleMediaItem> iterator = new GoogleMediaItemIteratorBuilder(connection)
                .withFilter(new DateTakenPhotoFilter(DateRange.forDay(2018, 11, 21)))
                .build();
        while (iterator.hasNext()) {
            GoogleMediaItem photo = iterator.next();
            System.err.println(photo.getFileName() + "  " + photo.getDateTimeTaken());
        }
    }

}
