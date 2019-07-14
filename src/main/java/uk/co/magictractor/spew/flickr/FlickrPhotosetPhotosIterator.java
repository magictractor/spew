package uk.co.magictractor.spew.flickr;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.flickr.FlickrPhotosetIterator.FlickrPhotosetIteratorBuilder;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhotoset;

/**
 * Lists the photos in a photoset.
 * https://www.flickr.com/services/api/flickr.photosets.getPhotos.html
 */
public class FlickrPhotosetPhotosIterator<E> extends PageCountServiceIterator<E> {

    private FlickrPhotosetPhotosIterator() {
    }

    private String photosetId;
    private String userId;

    @Override
    protected List<E> fetchPage(int pageNumber) {
        SpewRequest request = SpewRequest.createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photosets.getPhotos");

        request.setQueryStringParam("photoset_id", photosetId);
        request.setQueryStringParam("user_id", userId);

        request.setQueryStringParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        SpewResponse response = getConnection().request(request);

        System.err.println(response);

        setTotalItemCount(response.getInt("$.photoset.total"));
        setTotalPageCount(response.getInt("$.photoset.pages"));

        return response.getList("$.photoset.photo", getElementType());
    }

    public static class FlickrPhotosetPhotosIteratorBuilder<E>
            extends
            PageCountServiceIteratorBuilder<E, FlickrPhotosetPhotosIterator<E>, FlickrPhotosetIteratorBuilder<E>> {

        public FlickrPhotosetPhotosIteratorBuilder(SpewConnection connection, Class<E> elementType, String photosetId) {
            super(connection, elementType, new FlickrPhotosetPhotosIterator<>());
            getIteratorInstance().photosetId = photosetId;
        }
    }

    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyFlickrApp.class);
        Iterator<FlickrPhotoset> iter = new FlickrPhotosetIteratorBuilder<>(connection, FlickrPhotoset.class)
                .build();
        while (iter.hasNext()) {
            FlickrPhotoset photoset = iter.next();
            System.err.println(photoset);
        }
    }

}
