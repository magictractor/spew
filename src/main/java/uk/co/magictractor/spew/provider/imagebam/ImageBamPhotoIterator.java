package uk.co.magictractor.spew.provider.imagebam;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SingleCallServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.imagebam.MyImageBamApp;
import uk.co.magictractor.spew.example.imagebam.pojo.ImageBamPhoto;

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
        OutgoingHttpRequest request = createPostRequest(ImageBam.REST_ENDPOINT + "get_gallery_images");

        // TODO! set gallery id

        SpewParsedResponse response = request.sendRequest();

        System.err.println(response);

        return response.getList("$.rsp.images", getElementType());
    }

    public static class ImageBamPhotoIteratorBuilder<E> extends
            SingleCallServiceIteratorBuilder<E, ImageBamPhotoIterator<E>, ImageBamPhotoIteratorBuilder<E>> {

        protected ImageBamPhotoIteratorBuilder(SpewApplication<ImageBam> application, Class<E> elementType) {
            super(application, elementType, new ImageBamPhotoIterator<>());
        }
    }

    public static void main(String[] args) {
        Iterator<ImageBamPhoto> iterator = new ImageBamPhotoIteratorBuilder<>(new MyImageBamApp(), ImageBamPhoto.class)
                .build();
        while (iterator.hasNext()) {
            ImageBamPhoto photo = iterator.next();
            System.err.println(photo.getTitle() + "  " + photo.getDateTimeTaken());
        }
    }

}
