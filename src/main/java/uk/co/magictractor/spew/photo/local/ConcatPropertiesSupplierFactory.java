package uk.co.magictractor.spew.photo.local;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.magictractor.spew.photo.TagSet;

public class ConcatPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

    private final List<PhotoPropertiesSupplierFactory> wrapped = new ArrayList<>();

    public ConcatPropertiesSupplierFactory(PhotoPropertiesSupplierFactory... factories) {
        wrapped.addAll(Arrays.asList(factories));
    }

    public void add(PhotoPropertiesSupplierFactory factory) {
        wrapped.add(factory);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getFileNamePropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getFileNamePropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getTitlePropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getDescriptionPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getTagSetPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getDateTimeTakenPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getRatingPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getShutterSpeedPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getShutterSpeedPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<String>> getAperturePropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getAperturePropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getIsoPropertyValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getIsoPropertyValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getWidthValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getWidthValueSuppliers);
    }

    @Override
    public Stream<PhotoPropertiesSupplier<Integer>> getHeightValueSuppliers() {
        return concat(PhotoPropertiesSupplierFactory::getHeightValueSuppliers);
    }

    private <T> Stream<PhotoPropertiesSupplier<T>> concat(
            Function<PhotoPropertiesSupplierFactory, Stream<PhotoPropertiesSupplier<T>>> method) {
        return wrapped.stream().map(method).flatMap(i -> i);
    }
}
