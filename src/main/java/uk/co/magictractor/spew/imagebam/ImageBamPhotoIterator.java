package uk.co.magictractor.spew.imagebam;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.SingleCallServiceIterator;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.imagebam.pojo.ImageBamPhoto;

/**
 * It appears the the ImageBam API only supports listing Photos which are in a
 * gallery - there doesn't appear to be a way to list photos which are not in a
 * gallery, or to list all photos.
 */
public class ImageBamPhotoIterator<E> extends SingleCallServiceIterator<E> {

    private ImageBamPhotoIterator() {
    }

    @Override
    protected List<E> fetchPage() {
        SpewRequest request = SpewRequest.createPostRequest(ImageBam.REST_ENDPOINT + "get_gallery_images");

        // TODO! set gallery id

        SpewResponse response = getConnection().request(request);

        System.err.println(response);

        return response.getList("$.rsp.images", getElementType());
    }

    public static class ImageBamPhotoIteratorBuilder<E> extends
            SingleCallServiceIteratorBuilder<E, ImageBamPhotoIterator<E>, ImageBamPhotoIteratorBuilder<E>> {

        protected ImageBamPhotoIteratorBuilder(SpewConnection connection, Class<E> elementType) {
            super(connection, elementType, new ImageBamPhotoIterator<>());
        }
    }

    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyImageBamApp.class);
        Iterator<ImageBamPhoto> iterator = new ImageBamPhotoIteratorBuilder<>(connection, ImageBamPhoto.class).build();
        while (iterator.hasNext()) {
            ImageBamPhoto photo = iterator.next();
            System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
        }
    }

}
