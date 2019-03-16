package uk.co.magictractor.oauth.local;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

// Common code for images and sidecars
public abstract class BaseLocalPhoto implements Photo {

	private final Path path;

	private boolean propertyValuesRead;
	private String title;
	private String description;
	private TagSet tagSet;
	private Instant dateTimeTaken;
	private Integer rating;

	private String shutterSpeed;

	private String aperture;

	private Integer iso;

	protected BaseLocalPhoto(Path path) {
		this.path = path;
	}

	protected Path getPath() {
		return path;
	}

	@Override
	public String getServiceProviderId() {
		// Could use absolute path here
		return null;
	}

	@Override
	public String getFileName() {
		return path.getFileName().toString();
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
		preInit();
		title = getBestPropertyValue(getTitlePropertyValueSuppliers(), "title");
		description = getBestPropertyValue(getDescriptionPropertyValueSuppliers(), "description");
		// TODO! could merge tags rather than getting best
		tagSet = getBestPropertyValue(getTagSetPropertyValueSuppliers(), "tags");
		dateTimeTaken = getBestPropertyValue(getDateTimeTakenPropertyValueSuppliers(), "date time taken");
		rating = getBestPropertyValue(getRatingPropertyValueSuppliers(), "rating");
		postInit();
	}

	public void preInit() {
	}

	public void postInit() {
	}

	// First non-null value is best.
	private <T> T getBestPropertyValue(List<SupplierWithDescription<T>> propertyValueSuppliers, String description) {
		// List<SuppierWithDescription<T>> propertyValueSuppliers =
		// getPropertyValueSuppliers(photoPropertyType);

		if (propertyValueSuppliers == null || propertyValueSuppliers.isEmpty()) {
			System.err.println("getPropertyValueSuppliers() returned no Suppliers for property type " + description);
			return null;
		}

		T bestPropertyValue = null;
		for (SupplierWithDescription<T> propertyValueSupplier : propertyValueSuppliers) {
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
		}

		return bestPropertyValue;
	}

//	protected  <T> List<SuppierWithDescription<T>> getPropertyValueSuppliers(PhotoPropertyType photoPropertyType) {
//		List<SuppierWithDescription<T>> suppliers;
//		switch (photoPropertyType) {
//		case Title:
//			suppliers = getTitlePropertyValueSuppliers();
//			default: 
//				throw new IllegalStateException("Code needs to be modified to handle photo proprty type " + photoPropertyType);
//		}
//	}

	protected abstract List<SupplierWithDescription<String>> getTitlePropertyValueSuppliers();

	protected abstract List<SupplierWithDescription<String>> getDescriptionPropertyValueSuppliers();

	protected abstract List<SupplierWithDescription<TagSet>> getTagSetPropertyValueSuppliers();

	protected abstract List<SupplierWithDescription<Instant>> getDateTimeTakenPropertyValueSuppliers();

	protected abstract List<SupplierWithDescription<Integer>> getRatingPropertyValueSuppliers();

	// Supplier with a description for logging
	public static interface SupplierWithDescription<T> extends Supplier<T> {
		String getDescription();
	}
}
