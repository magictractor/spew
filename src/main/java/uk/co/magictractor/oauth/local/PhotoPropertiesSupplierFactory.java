package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.stream.Stream;

import uk.co.magictractor.oauth.common.TagSet;

/**
 * Implementations may return null, which will result in warnings. Use this for
 * work-in-progress implementations.
 * 
 * For properties which always have no value, implementations should return an
 * empty List.
 * 
 * Stream return type is used because the typical use case is to iterate once
 * over the suppliers, and likely stop at the first supplier which returns a
 * non-null value.
 */
public interface PhotoPropertiesSupplierFactory {

	Stream<PhotoPropertiesSupplier<String>> getFileNamePropertyValueSuppliers();
	
	Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers();

	Stream<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers();

	Stream<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers();

	Stream<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers();

	Stream<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers();

	Stream<PhotoPropertiesSupplier<String>> getShutterSpeedPropertyValueSuppliers();
	
	Stream<PhotoPropertiesSupplier<String>> getAperturePropertyValueSuppliers();
	
	Stream<PhotoPropertiesSupplier<Integer>> getIsoPropertyValueSuppliers();
	
	Stream<PhotoPropertiesSupplier<Integer>> getWidthValueSuppliers();
	
	Stream<PhotoPropertiesSupplier<Integer>> getHeightValueSuppliers();

}
