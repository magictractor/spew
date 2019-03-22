package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.List;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public interface PhotoPropertiesSupplierFactory {

	List<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers();

	List<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers();

}
