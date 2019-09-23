package uk.co.magictractor.spew.photo.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.local.ConcatPropertiesSupplierFactory;
import uk.co.magictractor.spew.photo.local.PhotoPropertiesSupplier;
import uk.co.magictractor.spew.photo.local.PhotoPropertiesSupplierFactory;
import uk.co.magictractor.spew.photo.local.PropertySuppliedPhoto;

public class ConcatPropertiesSupplierFactoryTest {

    private static final PhotoPropertiesSupplierFactory A = new NoopPhotoPropertiesSupplierFactory() {

        @Override
        public Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
            return Stream.of(SimplePhotoPropertiesSupplier.of("Title A"));
        }
    };

    private static final PhotoPropertiesSupplierFactory B = new NoopPhotoPropertiesSupplierFactory() {

        @Override
        public Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
            return Stream.of(SimplePhotoPropertiesSupplier.of("Title B"));
        }
    };

    @Test
    public void t() {
        ConcatPropertiesSupplierFactory ab = new ConcatPropertiesSupplierFactory(A, B);
        Image image = PropertySuppliedPhoto.forFactory(ab);
        assertThat(image.getTitle()).isEqualTo("Title A");
    }

}
