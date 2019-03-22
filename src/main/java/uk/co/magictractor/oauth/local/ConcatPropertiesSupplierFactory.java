package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public class ConcatPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

	// TODO! streams rather than lists??
	private final List<PhotoPropertiesSupplierFactory> wrapped = new ArrayList<>();

	@Override
	public List<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
		return concat(PhotoPropertiesSupplierFactory::getTitlePropertyValueSuppliers);
	}

	@Override
	public List<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers() {
		return concat(PhotoPropertiesSupplierFactory::getDescriptionPropertyValueSuppliers);
	}

	@Override
	public List<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers() {
		return concat(PhotoPropertiesSupplierFactory::getTagSetPropertyValueSuppliers);
	}

	@Override
	public List<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers() {
		return concat(PhotoPropertiesSupplierFactory::getDateTimeTakenPropertyValueSuppliers);
	}

	@Override
	public List<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers() {
		return concat(PhotoPropertiesSupplierFactory::getRatingPropertyValueSuppliers);
	}

	private <T> List<PhotoPropertiesSupplier<T>> concat(
			Function<PhotoPropertiesSupplierFactory, List<PhotoPropertiesSupplier<T>>> method) {
		return wrapped.stream().map(method).flatMap(Collection::stream).collect(Collectors.toList());
	}
}
