package uk.co.magictractor.oauth.local;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class LocalPhoto extends PropertySuppliedPhoto {

	/*
	 * Files encountered which have a suffix in neither of these lists results in a
	 * warning, and are assumed not to be photos.
	 */
	// TODO! this is fine for me, but does not cover all cases
	private static final List<String> PHOTO_SUFFIXES = Arrays.asList("jpg", "rw2", "cr2");
	private static final List<String> NON_PHOTO_SUFFIXES = Arrays.asList("xmp", "mp4");

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

	@Override
	protected PhotoPropertiesSupplierFactory getPhotoPropertiesSupplierFactory() {
		// TODO! sidecar props first
		return new ExifPropertiesSupplierFactory(getPath());
	}

}
