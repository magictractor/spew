package uk.co.magictractor.oauth.local;

import java.time.Instant;
import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

/**
 * For photographs where title, description, rating etc can be derived from
 * multiple sources such as exif data in images and sidecar data.
 */
public abstract class PropertySuppliedPhoto implements Photo {

	private boolean propertyValuesRead;
	private String title;
	private String description;
	private TagSet tagSet;
	private Instant dateTimeTaken;
	private Integer rating;

	private String shutterSpeed;

	private String aperture;

	private Integer iso;

//	protected PropertySuppliedPhoto(Path path) {
//		this.path = path;
//	}
//
//	protected Path getPath() {
//		return path;
//	}

	@Override
	public String getServiceProviderId() {
		// Could use absolute path here - or property providers
		return null;
	}

	@Override
	public String getFileName() {
		// TODO! use property providers
		// return path.getFileName().toString();
		return null;
	}

	@Override
	public String getTitle() {
		ensurePropertyValuesRead();
		return title;
	}

	@Override
	public String getDescription() {
		ensurePropertyValuesRead();
		return description;
	}

	@Override
	public TagSet getTagSet() {
		ensurePropertyValuesRead();
		return tagSet;
	}

	@Override
	public Instant getDateTimeTaken() {
		ensurePropertyValuesRead();
		return dateTimeTaken;
	}

	@Override
	public final Instant getDateTimeUpload() {
		// Always null. Local files might never have been uploaded, or have been
		// uploaded multiple times and to multiple service providers.
		return null;
	}

	@Override
	public Integer getRating() {
		ensurePropertyValuesRead();
		return rating;
	}

	@Override
	public String getShutterSpeed() {
		ensurePropertyValuesRead();
		return shutterSpeed;
	}

	@Override
	public String getAperture() {
		ensurePropertyValuesRead();
		return aperture;
	}

	@Override
	public Integer getIso() {
		ensurePropertyValuesRead();
		return iso;
	}

	private void ensurePropertyValuesRead() {
		if (!propertyValuesRead) {
			initPropertyValues();
		}
	}

	private void initPropertyValues() {
		PhotoPropertiesSupplierFactory supplierFactory = getPhotoPropertiesSupplierFactory();

		title = getBestPropertyValue(supplierFactory.getTitlePropertyValueSuppliers(), "title");
		description = getBestPropertyValue(supplierFactory.getDescriptionPropertyValueSuppliers(), "description");
		tagSet = getBestPropertyValue(supplierFactory.getTagSetPropertyValueSuppliers(), "tags");
		dateTimeTaken = getBestPropertyValue(supplierFactory.getDateTimeTakenPropertyValueSuppliers(),
				"date time taken");
		rating = getBestPropertyValue(supplierFactory.getRatingPropertyValueSuppliers(), "rating");
	}

	protected abstract PhotoPropertiesSupplierFactory getPhotoPropertiesSupplierFactory();

	// First non-null value is best.
	private <T> T getBestPropertyValue(Stream<PhotoPropertiesSupplier<T>> propertyValueSuppliers, String description) {
		// List<SuppierWithDescription<T>> propertyValueSuppliers =
		// getPropertyValueSuppliers(photoPropertyType);

		/**
		 * Null means not implemented and causes a warning. When there is no
		 * corresponding property, implementations should return an empty list.
		 */
		if (propertyValueSuppliers == null) {
			System.err.println("missing property suppliers for " + description);
			return null;
		}

		T bestPropertyValue = null;
		// propertyValueSuppliers.iterator()
		// for (PhotoPropertiesSupplier<T> propertyValueSupplier :
		// propertyValueSuppliers) {
		// propertyValueSuppliers.forEach((propertyValueSupplier) -> {

		Iterator<PhotoPropertiesSupplier<T>> iter = propertyValueSuppliers.iterator();
		while (iter.hasNext()) {
			PhotoPropertiesSupplier<T> propertyValueSupplier = iter.next();
			T value = propertyValueSupplier.get();

			System.err.println(propertyValueSupplier.getDescription() + " -> " + value);

			if (value != null) {
				if (bestPropertyValue == null) {
					// First non-null value is best.
					bestPropertyValue = value;
					// TODO! could check log level here and return immediately if not logging
					// different values.
				} else if (!value.equals(bestPropertyValue)) {
					// TODO! improve this
					System.err.println("Different value from " + propertyValueSupplier.getDescription());
				}
			}
			// });
		}

		return bestPropertyValue;
	}

	// Supplier with a description for logging
	public static interface PhotoPropertiesSupplier<T> extends Supplier<T> {
		String getDescription();
	}

	// For simple implementations - just unit tests??
	public static PropertySuppliedPhoto forFactory(PhotoPropertiesSupplierFactory factory) {
		return new PropertySuppliedPhoto() {

			@Override
			protected PhotoPropertiesSupplierFactory getPhotoPropertiesSupplierFactory() {
				return factory;
			}
		};
	}
}