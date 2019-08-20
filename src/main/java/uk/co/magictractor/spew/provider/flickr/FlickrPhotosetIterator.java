package uk.co.magictractor.spew.provider.flickr;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhotoset;

/**
 * Lists albums and collections (collectively known as photosets in the API).
 * https://www.flickr.com/services/api/flickr.photosets.getList.htm
 */
public class FlickrPhotosetIterator<E> extends PageCountServiceIterator<E> {

    private FlickrPhotosetIterator() {
    }

    private String userId;

    @Override
    protected List<E> fetchPage(int pageNumber) {
        OutgoingHttpRequest request = getApplication().createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photosets.getList");

        request.setQueryStringParam("user_id", userId);

        request.setQueryStringParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        SpewParsedResponse response = request.sendRequest();

        System.err.println(response);

        setTotalItemCount(response.getInt("$.photosets.total"));
        setTotalPageCount(response.getInt("$.photosets.pages"));

        return response.getList("$.photosets.photoset", getElementType());
    }

    public static class FlickrPhotosetIteratorBuilder<E>
            extends PageCountServiceIteratorBuilder<E, FlickrPhotosetIterator<E>, FlickrPhotosetIteratorBuilder<E>> {

        public FlickrPhotosetIteratorBuilder(SpewApplication application, Class<E> elementType) {
            super(application, elementType, new FlickrPhotosetIterator<>());
        }

        public FlickrPhotosetIteratorBuilder<E> withUserId(String userId) {
            getIteratorInstance().userId = userId;
            return this;
        }
    }

    public static void main(String[] args) {
        Iterator<FlickrPhotoset> iter = new FlickrPhotosetIteratorBuilder<>(new MyFlickrApp(), FlickrPhotoset.class)
                .build();
        while (iter.hasNext()) {
            FlickrPhotoset photoset = iter.next();
            System.err.println(photoset);
        }
    }

}
