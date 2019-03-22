package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.stream.Stream;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public class NoopPhotoPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

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

}
