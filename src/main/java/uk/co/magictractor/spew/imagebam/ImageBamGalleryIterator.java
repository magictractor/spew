package uk.co.magictractor.spew.imagebam;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.SingleCallServiceIterator;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.imagebam.pojo.ImageBamGallery;

public class ImageBamGalleryIterator<E> extends SingleCallServiceIterator<E> {

    private ImageBamGalleryIterator() {
    }

    @Override
    protected List<E> fetchPage() {
        SpewRequest request = SpewRequest.createGetRequest(ImageBam.REST_ENDPOINT + "get_galleries");

        SpewResponse response = getConnection().request(request);

        System.err.println(response);

        return response.getList("$.rsp.galleries", getElementType());
    }

    public static class ImageBamGalleryIteratorBuilder<E> extends
            SingleCallServiceIteratorBuilder<E, ImageBamGalleryIterator<E>, ImageBamGalleryIteratorBuilder<E>> {

        protected ImageBamGalleryIteratorBuilder(SpewConnection connection, Class<E> elementType) {
            super(connection, elementType, new ImageBamGalleryIterator<>());
        }
    }

    public static void main(String[] args) {
        SpewConnection connection = SpewConnectionFactory.getConnection(MyImageBamApp.class);
        Iterator<ImageBamGallery> iterator = new ImageBamGalleryIteratorBuilder<>(connection, ImageBamGallery.class)
                .build();
        while (iterator.hasNext()) {
            ImageBamGallery gallery = iterator.next();
            System.err.println(gallery.toString());
        }
    }

}
