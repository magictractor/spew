package uk.co.magictractor.oauth.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.local.ConcatPropertiesSupplierFactory;
import uk.co.magictractor.spew.local.PhotoPropertiesSupplier;
import uk.co.magictractor.spew.local.PhotoPropertiesSupplierFactory;
import uk.co.magictractor.spew.local.PropertySuppliedPhoto;

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
        Photo photo = PropertySuppliedPhoto.forFactory(ab);
        assertThat(photo.getTitle()).isEqualTo("Title A");
    }

}
