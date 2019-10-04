package uk.co.magictractor.spew.provider.imagebam;

import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SingleCallServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.imagebam.MyImageBamApp;
import uk.co.magictractor.spew.example.imagebam.pojo.ImageBamGallery;

public class ImageBamGalleryIterator<E> extends SingleCallServiceIterator<E> {

    private ImageBamGalleryIterator() {
    }

    @Override
    protected List<E> fetchPage() {
        ApplicationRequest request = createGetRequest(ImageBam.REST_ENDPOINT + "get_galleries");

        SpewParsedResponse response = request.sendRequest();

        System.err.println(response);

        return response.getList("$.rsp.galleries", getElementType());
    }

    public static class ImageBamGalleryIteratorBuilder<E> extends
            SingleCallServiceIteratorBuilder<E, ImageBamGalleryIterator<E>, ImageBamGalleryIteratorBuilder<E>> {

        protected ImageBamGalleryIteratorBuilder(SpewApplication<ImageBam> application, Class<E> elementType) {
            super(application, elementType, new ImageBamGalleryIterator<>());
        }
    }

    public static void main(String[] args) {
        Iterator<ImageBamGallery> iterator = new ImageBamGalleryIteratorBuilder<>(new MyImageBamApp(),
            ImageBamGallery.class)
                    .build();
        while (iterator.hasNext()) {
            ImageBamGallery gallery = iterator.next();
            System.err.println(gallery.toString());
        }
    }

}
