package uk.co.magictractor.spew.imagebam;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.OAuthConnection;
import uk.co.magictractor.spew.api.OAuthRequest;
import uk.co.magictractor.spew.api.OAuthResponse;
import uk.co.magictractor.spew.api.PageCountServiceIterator;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhotos;
import uk.co.magictractor.spew.imagebam.pojo.ImageBamPhoto;

public class ImageBamPhotoIterator extends PageCountServiceIterator<ImageBamPhoto> {

    private ImageBamPhotoIterator() {
    }

    @Override
    protected List<ImageBamPhoto> fetchPage(int pageNumber) {
        OAuthRequest request = OAuthRequest.createPostRequest(ImageBam.REST_ENDPOINT);

        // TODO! obviously incomplete - this is a Flickr method not Imagebam
        request.setParam("method", "flickr.photos.search");

        request.setParam("user_id", "me");
        request.setParam("sort", "date-taken-desc");
        request.setParam("page", pageNumber);
        // default is 100, max is 500
        // request.setParam("per_page", 500);

        OAuthResponse response = getConnection().request(request);

        System.err.println(response);

        FlickrPhotos photos = response.getObject("photos", FlickrPhotos.class);
        setTotalItemCount(photos.total);
        setTotalPageCount(photos.pages);

        // return photos.photo;

        return Collections.emptyList();
    }

    public static class ImageBamPhotoIteratorBuilder extends
            PageCountServiceIteratorBuilder<ImageBamPhoto, ImageBamPhotoIterator, ImageBamPhotoIteratorBuilder> {

        protected ImageBamPhotoIteratorBuilder(OAuthConnection connection) {
            super(connection, new ImageBamPhotoIterator());
        }
    }

    public static void main(String[] args) {
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyImageBamApp.class);
        Iterator<ImageBamPhoto> iterator = new ImageBamPhotoIteratorBuilder(connection).build();
        while (iterator.hasNext()) {
            ImageBamPhoto photo = iterator.next();
            System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
        }
    }

}