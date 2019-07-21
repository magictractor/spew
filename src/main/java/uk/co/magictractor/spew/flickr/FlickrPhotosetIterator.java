package uk.co.magictractor.spew.flickr;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhotoset;

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
        SpewRequest request = SpewRequest.createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photosets.getList");

        request.setQueryStringParam("user_id", userId);

        request.setQueryStringParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        SpewParsedResponse response = getConnection().request(request);

        System.err.println(response);

        setTotalItemCount(response.getInt("$.photosets.total"));
        setTotalPageCount(response.getInt("$.photosets.pages"));

        return response.getList("$.photosets.photoset", getElementType());
    }

    public static class FlickrPhotosetIteratorBuilder<E>
            extends PageCountServiceIteratorBuilder<E, FlickrPhotosetIterator<E>, FlickrPhotosetIteratorBuilder<E>> {

        public FlickrPhotosetIteratorBuilder(SpewConnection connection, Class<E> elementType) {
            super(connection, elementType, new FlickrPhotosetIterator<>());
        }

        public FlickrPhotosetIteratorBuilder<E> withUserId(String userId) {
            getIteratorInstance().userId = userId;
            return this;
        }
    }

    public static void main(String[] args) {
        SpewConnection connection = SpewConnectionFactory.getConnection(MyFlickrApp.class);
        Iterator<FlickrPhotoset> iter = new FlickrPhotosetIteratorBuilder<>(connection, FlickrPhotoset.class)
                .build();
        while (iter.hasNext()) {
            FlickrPhotoset photoset = iter.next();
            System.err.println(photoset);
        }
    }

}
