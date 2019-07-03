package uk.co.magictractor.oauth.google;

import java.util.Iterator;
import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.google.pojo.GoogleFilters;
import uk.co.magictractor.oauth.google.pojo.GoogleMediaItem;
import uk.co.magictractor.oauth.local.dates.DateRange;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<GoogleMediaItem> {

    private DateRange dateTakenRange;

    private GoogleMediaItemIterator() {
    }

    @Override
    protected OAuthRequest createPageRequest() {
        OAuthRequest request = OAuthRequest
                .createPostRequest("https://photoslibrary.googleapis.com/v1/mediaItems:search");

        if (dateTakenRange != null) {
            request.setParam("filters", new GoogleFilters(dateTakenRange));
        }

        return request;
    }

    @Override
    protected List<GoogleMediaItem> parsePageResponse(OAuthResponse response) {
        return response.getObject("mediaItems", new TypeRef<List<GoogleMediaItem>>() {
        });
    }

    public static class GoogleMediaItemIteratorBuilder extends
            GoogleServiceIteratorBuilder<GoogleMediaItem, GoogleMediaItemIterator, GoogleMediaItemIteratorBuilder> {

        public GoogleMediaItemIteratorBuilder() {
            super(new GoogleMediaItemIterator());
            addServerSideFilterHandler(DateTakenPhotoFilter.class, this::setDateTakenPhotoFilter);
        }

        private void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
            getIteratorInstance().dateTakenRange = filter.getDateRange();
        }

    }

    public static void main(String[] args) {
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyGooglePhotosApp.class);
        Iterator<GoogleMediaItem> iterator = new GoogleMediaItemIteratorBuilder()
                .withConnection(connection)
                .withFilter(new DateTakenPhotoFilter(DateRange.forDay(2018, 11, 21)))
                .build();
        while (iterator.hasNext()) {
            GoogleMediaItem photo = iterator.next();
            System.err.println(photo.getFileName() + "  " + photo.getDateTimeTaken());
        }
    }

}
