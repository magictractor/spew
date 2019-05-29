package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.stream.Stream;

import uk.co.magictractor.oauth.common.TagSet;

public class NoopPhotoPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getFileNamePropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getShutterSpeedPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getAperturePropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getIsoPropertyValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getWidthValueSuppliers() {
        return Stream.empty();
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getHeightValueSuppliers() {
        return Stream.empty();
    }

}
