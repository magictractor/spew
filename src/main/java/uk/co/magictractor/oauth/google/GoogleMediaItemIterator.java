package uk.co.magictractor.oauth.google;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PhotoIterator;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;
import uk.co.magictractor.oauth.google.pojo.GoogleFilters;
import uk.co.magictractor.oauth.google.pojo.GoogleMediaItem;
import uk.co.magictractor.oauth.local.dates.DateRange;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator extends GoogleServiceIterator<GoogleMediaItem>
        implements PhotoIterator<GoogleMediaItem>
{

    private static final List<Class<? extends PhotoFilter>> SUPPORTED_FILTERS = Arrays
            .asList(DateTakenPhotoFilter.class);

    private DateRange dateTakenRange;

    @Override
    public Collection<Class<? extends PhotoFilter>> supportedPhotoFilters() {
        return SUPPORTED_FILTERS;
    }

    @Override
    public void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
        dateTakenRange = filter.getDateRange();
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

    public static void main(String[] args) {
        GoogleMediaItemIterator iter = new GoogleMediaItemIterator();
        iter.addFilter(new DateTakenPhotoFilter(DateRange.forDay(2018, 11, 21)));
        while (iter.hasNext()) {
            GoogleMediaItem photo = iter.next();
            System.err.println(photo.getFileName() + "  " + photo.getDateTimeTaken());
        }
    }

}
