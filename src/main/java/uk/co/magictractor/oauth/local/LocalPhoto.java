package uk.co.magictractor.oauth.local;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.xmp.XmpDirectory;
import com.google.common.collect.Iterables;

import uk.co.magictractor.oauth.common.TagSet;

public class LocalPhoto extends BaseLocalPhoto {

	/*
	 * Files encountered which have a suffix in neither of these lists results in a
	 * warning, and are assumed not to be photos.
	 */
	// TODO! this is fine for me, but does not cover all cases
	private static final List<String> PHOTO_SUFFIXES = Arrays.asList("jpg", "rw2", "cr2");
	private static final List<String> NON_PHOTO_SUFFIXES = Arrays.asList("xmp", "mp4");

	private ExifSubIFDDirectory exif;
	private XmpDirectory xmp;

	// TODO! could have two methods - one just using file name, another using magic
	// to get file type?
	// see FileTypeDetector
	public static boolean isPhoto(Path path) {
		String fileName = path.getFileName().toString();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		if (PHOTO_SUFFIXES.contains(extension)) {
			return true;
		}
		if (NON_PHOTO_SUFFIXES.contains(extension)) {
			return false;
		}
		System.err.println("Unrecognised file type for: " + fileName);
		return false;
	}

	public LocalPhoto(Path path) {
		super(path);
	}

	private Path findSidecar() {
		String sidecarName = getPath().getFileName().toString() + ".xmp";

		// TODO! could also handle replacing extension with ".xmp" as well as appending
		// ".xmp"
		Path sidecarPath = getPath().resolveSibling(sidecarName);

		return java.nio.file.Files.exists(sidecarPath) ? sidecarPath : null;
	}

	// Photos are read on demand to avoid a performance hit when loading the local
	// library.
	// For performance files are read once and results cached.
	@Override
	public void preInit() {
		Metadata metadata;
		try {
			metadata = ImageMetadataReader.readMetadata(getPath().toFile());
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
	public void postInit() {
		exif = null;
		xmp = null;
	}

	@Override
	protected List<SupplierWithDescription<String>> getTitlePropertyValueSuppliers() {
		// return Arrays.asList(new ExifValueSupplier(ExifSubIFDDirectory.tit));
		return Collections.emptyList();
	}

	@Override
	protected List<SupplierWithDescription<String>> getDescriptionPropertyValueSuppliers() {
		return Collections.emptyList();
	}

	@Override
	protected List<SupplierWithDescription<TagSet>> getTagSetPropertyValueSuppliers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<SupplierWithDescription<Instant>> getDateTimeTakenPropertyValueSuppliers() {
		// TODO! what's the difference between original and digitized?
		// return Arrays.asList(new
		// ExifValueStringSupplier(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

		return null;
	}

	@Override
	protected List<SupplierWithDescription<Integer>> getRatingPropertyValueSuppliers() {
		return Arrays.asList(asInteger(new XmpValueSupplier("xmp:Rating")));
	}

	private SupplierWithDescription<Integer> asInteger(SupplierWithDescription<String> stringSupplier) {
		return new ConvertedSupplierWithDescription<Integer>(stringSupplier, (s) -> Integer.valueOf(s));
	}

	public class ConvertedSupplierWithDescription<T> implements SupplierWithDescription<T> {
		private SupplierWithDescription<String> wrapped;
		private Function<String, T> converter;

		public ConvertedSupplierWithDescription(SupplierWithDescription<String> wrapped,
				Function<String, T> converter) {
			this.wrapped = wrapped;
			this.converter = converter;
		}

		@Override
		public T get() {
			String string = wrapped.get();
			return string == null ? null : converter.apply(string);
		}

		@Override
		public String getDescription() {
			return wrapped.getDescription();
		}
	}

	public abstract class ExifValueSupplier<T> implements SupplierWithDescription<T> {
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

//	public class ExifIntegerValueSupplier extends ExifValueSupplier<Integer> {
//		ExifIntegerValueSupplier(int tagType) {
//			super(tagType);
//		}
//
//		@Override
//		public Integer get() {
//			// TODO! exif/xmp
//			return xmp.getInteger(tagType);
//		}
//	}

	public class XmpValueSupplier implements SupplierWithDescription<String> {

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
