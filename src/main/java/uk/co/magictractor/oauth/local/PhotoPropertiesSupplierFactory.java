package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.List;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

/**
 * Implementations may return null, which will result in warnings. Use this for
 * work-in-progress implementations.
 * 
 * For properties which always have no value, implementations should return an
 * empty List.
 */
public interface PhotoPropertiesSupplierFactory {

	List<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers();

	List<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers();

	List<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers();

}
