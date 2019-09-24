package uk.co.magictractor.spew.provider.google;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.google.MyGooglePhotosApp;
import uk.co.magictractor.spew.example.google.pojo.GoogleImage;
import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.filter.DateTakenPhotoFilter;
import uk.co.magictractor.spew.photo.local.dates.DateRange;

// https://developers.google.com/photos/library/guides/list
//
// https://developers.google.com/photos/library/reference/rest/v1/mediaItems#MediaItem
public class GoogleMediaItemIterator<E> extends GoogleServiceIterator<E> {

    private DateRange dateTakenRange;

    private GoogleMediaItemIterator() {
    }

    @Override
    protected OutgoingHttpRequest createPageRequest() {
        OutgoingHttpRequest request = createPostRequest("https://photoslibrary.googleapis.com/v1/mediaItems:search");

        if (dateTakenRange != null) {
            request.setBodyParam("filters", new GoogleFilters(dateTakenRange));
        }

        return request;
    }

    @Override
    protected List<E> parsePageResponse(SpewParsedResponse response) {
        return response.getList("mediaItems", getElementType());
    }

    public static class GoogleMediaItemIteratorBuilder<E> extends
            GoogleServiceIteratorBuilder<E, GoogleMediaItemIterator<E>, GoogleMediaItemIteratorBuilder<E>> {

        public GoogleMediaItemIteratorBuilder(SpewApplication<Google> application, Class<E> elementType) {
            super(application, elementType, new GoogleMediaItemIterator<E>());
            addServerSideFilterHandler(DateTakenPhotoFilter.class, this::setDateTakenPhotoFilter);
        }

        private void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
            getIteratorInstance().dateTakenRange = filter.getDateRange();
        }

    }

    public static void main(String[] args) {
        Iterator<GoogleImage> iterator = new GoogleMediaItemIteratorBuilder<>(new MyGooglePhotosApp(),
            GoogleImage.class)
                    .withFilter(new DateTakenPhotoFilter(DateRange.forDay(2018, 11, 21)))
                    .build();
        while (iterator.hasNext()) {
            Image image = iterator.next();
            System.err.println(image.getFileName() + "  " + image.getDateTimeTaken());
        }
    }

}
