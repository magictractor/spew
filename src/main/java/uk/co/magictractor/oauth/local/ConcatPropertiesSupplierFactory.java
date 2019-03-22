package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public class ConcatPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

	// TODO! streams rather than lists??
	private final List<PhotoPropertiesSupplierFactory> wrapped = new ArrayList<>();

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

	private <T> Stream<PhotoPropertiesSupplier<T>> concat(
			Function<PhotoPropertiesSupplierFactory, Stream<PhotoPropertiesSupplier<T>>> method) {
		// return
		// wrapped.stream().map(method).flatMap(Collection::stream).collect(Collectors.toList());
		return wrapped.stream().map(method).flatMap(i -> i);
		// throw new UnsupportedOperationException();
	}
}
