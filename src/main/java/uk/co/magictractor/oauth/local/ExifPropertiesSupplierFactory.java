package uk.co.magictractor.oauth.local;

import static uk.co.magictractor.oauth.local.ConvertedPhotoPropertiesSupplier.asInteger;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.stream.Stream;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.google.common.collect.Iterables;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.PropertySuppliedPhoto.PhotoPropertiesSupplier;

public class ExifPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

	private final Path path;
	private ExifSubIFDDirectory exif;
	private XmpDirectory xmp;

	public ExifPropertiesSupplierFactory(Path path) {
		this.path = path;
		// TODO! init on demand
		init();
	}

	// Photos are read on demand to avoid a performance hit when loading the local
	// library.
	// For performance files are read once and results cached.
	private void init() {
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(path.toFile());
		} catch (ImageProcessingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		// exif = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class)
		exif = Iterables.getOnlyElement(metadata.getDirectoriesOfType(ExifSubIFDDirectory.class));
		// xmp often not present
		// xmp =
		// Iterables.getOnlyElement(metadata.getDirectoriesOfType(XmpDirectory.class));
		xmp = metadata.getFirstDirectoryOfType(XmpDirectory.class);

//		for (Directory directory : metadata.getDirectories()) {
//			String directoryName = directory.getName();
//			System.err.println(">>> " + directoryName);
//			for (Tag tag : directory.getTags()) {
//				String tagName = tag.getTagName();
//				String description = tag.getDescription();
//				System.err.println(tagName + ": " + description);
//			}
//		}
//
//		System.err.println(metadata);

		// Exif IFDO contains camera make and model
		// lens in panasonic maker notes
		// Exif SubIFD contains exposure time, f-number, iso, exposure program, exposure
		// bias value, metering mode, focal length, (exif image) width, height
		// metadata.
		Directory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
		System.err.println("aperture:  " + subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_TIME)); // null
		System.err.println("shutter:   " + subIFD.getString(ExifSubIFDDirectory.TAG_FNUMBER)); // null 6.3 5.6 10
		System.err.println("iso:       " + subIFD.getString(ExifSubIFDDirectory.TAG_ISO_EQUIVALENT)); // 320
		System.err.println("exp prog:  " + subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_PROGRAM)); // 3
		System.err.println("exp bias:  " + subIFD.getString(ExifSubIFDDirectory.TAG_EXPOSURE_BIAS)); // 0 -33/50
		System.err.println("focal len: " + subIFD.getString(ExifSubIFDDirectory.TAG_FOCAL_LENGTH)); // 400
		System.err.println("width:     " + subIFD.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH)); // 5184
		System.err.println("height:    " + subIFD.getString(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT));
		// what's the difference between original and digitized?
		System.err.println("datetime:  " + subIFD.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

//		for (Directory dir : metadata.getDirectories()) {
//			// dir.
//			for (Tag tag : dir.getTags()) {
//				System.err.println("tag: " + tag);
//			}
//		}
//
//		if (xmp != null) {
//			System.err.println("xmp props: " + xmp.getXmpProperties());
//			System.err.println("xmp rating: " + xmp.getXmpProperties().get("xmp:Rating"));
//		}

		// System.err.println("datetime: " +
		// subIFD.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

		// and file name from file (or File directory in metadata)
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
		// TODO! what's the difference between original and digitized?
		// return Arrays.asList(new
		// ExifValueStringSupplier(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));
// TODO! null so that a warning is logged until impl is added
		return null;
	}

	@Override
	public Stream<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers() {
		return Stream.of(asInteger(new XmpValueSupplier("xmp:Rating")));
	}

	public abstract class ExifValueSupplier<T> implements PhotoPropertiesSupplier<T> {
		protected final int tagType;

		ExifValueSupplier(int tagType) {
			this.tagType = tagType;
		}

		@Override
		public final String getDescription() {
			return exif.getDescription(tagType);
		}
	}

	public class ExifStringValueSupplier extends ExifValueSupplier<String> {
		ExifStringValueSupplier(int tagType) {
			super(tagType);
		}

		@Override
		public String get() {
			return exif.getString(tagType);
		}
	}

	public class XmpValueSupplier implements PhotoPropertiesSupplier<String> {

		private String key;

		XmpValueSupplier(String key) {
			this.key = key;
		}

		@Override
		public String get() {
			return xmp == null ? null : xmp.getXmpProperties().get(key);
		}

		@Override
		public String getDescription() {
			return "[XMP] " + key;
		}
	}
}
