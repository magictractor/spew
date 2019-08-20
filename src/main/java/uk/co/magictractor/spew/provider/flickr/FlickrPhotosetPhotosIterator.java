package uk.co.magictractor.spew.provider.flickr;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhotoset;
import uk.co.magictractor.spew.provider.flickr.FlickrPhotosetIterator.FlickrPhotosetIteratorBuilder;

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
        OutgoingHttpRequest request = getApplication().createPostRequest(Flickr.REST_ENDPOINT);

        request.setQueryStringParam("method", "flickr.photosets.getPhotos");

        request.setQueryStringParam("photoset_id", photosetId);
        request.setQueryStringParam("user_id", userId);

        request.setQueryStringParam("extras", FlickrPhotoIterator.EXTRAS);

        request.setQueryStringParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        SpewParsedResponse response = request.sendRequest();

        System.err.println(response);

        setTotalItemCount(response.getInt("$.photoset.total"));
        setTotalPageCount(response.getInt("$.photoset.pages"));

        return response.getList("$.photoset.photo", getElementType());
    }

    public static class FlickrPhotosetPhotosIteratorBuilder<E>
            extends
            PageCountServiceIteratorBuilder<E, FlickrPhotosetPhotosIterator<E>, FlickrPhotosetIteratorBuilder<E>> {

        public FlickrPhotosetPhotosIteratorBuilder(SpewApplication<Flickr> application, Class<E> elementType,
                String photosetId) {
            super(application, elementType, new FlickrPhotosetPhotosIterator<>());
            getIteratorInstance().photosetId = photosetId;
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
