package uk.co.magictractor.oauth.local;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPIterator;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.properties.XMPProperty;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class LocalPhoto implements Photo {

	/*
	 * Files encountered which have a suffix in neither of these lists results in a
	 * warning, and are assumed not to be photos.
	 */
	// TODO! this is fine for me, but does not cover all cases
	private static final List<String> PHOTO_SUFFIXES = Arrays.asList("jpg", "rw2", "cr2");
	private static final List<String> NON_PHOTO_SUFFIXES = Arrays.asList("xmp", "mp4");

	private final Path photoPath;
	private boolean hasReadExif;
	
	// These values read from exif/sidecar on demand
	private String title;
	private String description;
	private LocalDateTime dateTimeTaken;
	private String shutterSpeed;
	private String aperture;
	private int iso;
	private TagSet tagSet;

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
		this.photoPath = path;
	}

	@Override
	public String getFileName() {
		return photoPath.getFileName().toString();
	}

	@Override
	public String getTitle() {
		ensureFileRead();
		return title;
	}
	
	@Override
	public String getDescription() {
		ensureFileRead();
		return description;
	}
	
	@Override
	public LocalDateTime getDateTimeTaken() {
		ensureFileRead();
		return dateTimeTaken;
	}

	@Override
	public String getShutterSpeed() {
		ensureFileRead();
		return shutterSpeed;
	}

	@Override
	public String getAperture() {
		ensureFileRead();
		return aperture;
	}

	@Override
	public int getIso() {
		ensureFileRead();
		return iso;
	}

	@Override
	public TagSet getTagSet() {
		ensureFileRead();
		return tagSet;
	}

	private void ensureFileRead() {
		if (hasReadExif) {
			return;
		}

		readPhotoExif();

		Path sidecarPath = findSidecar();
		if (sidecarPath != null) {
			// TODO! remove throws
			ExceptionUtil.call(() -> readSidecarExif(sidecarPath));
		}

		hasReadExif = true;
	}

	private Path findSidecar() {
		String sidecarName = photoPath.getFileName().toString() + ".xmp";

		// TODO! could also handle replacing extension with ".xmp" as well as appending
		// ".xmp"
		Path sidecarPath = photoPath.resolveSibling(sidecarName);

		return java.nio.file.Files.exists(sidecarPath) ? sidecarPath : null;
	}

	// Photos are read on demand to avoid a performance hit when loading the local
	// library.
	// For performance files are read once and results cached.
	private void readPhotoExif() {
		Metadata metadata;

		try {
			metadata = ImageMetadataReader.readMetadata(photoPath.toFile());
		} catch (ImageProcessingException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

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

		// System.err.println("datetime: " +
		// subIFD.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL));

		// and file name from file (or File directory in metadata)
	}

	/**
	 * MetadataExtractor does not currently support reading values.
	 * 
	 * This ticket includes the workaround used here.
	 * https://github.com/drewnoakes/metadata-extractor/issues/118
	 * 
	 * @throws XMPException
	 * @throws IOException
	 */
	private void readSidecarExif(Path sidecarPath) throws XMPException, IOException {
		XMPMeta xmpMeta;

		try (InputStream sidecarStream = Files.newInputStream(sidecarPath)) {
			xmpMeta = XMPMetaFactory.parse(sidecarStream);
		}

		// Unregistered schema namespace URI
		// XMPProperty subject = xmpMeta.getProperty("dc", "subject");

		// Empty schema namespace URI
		// XMPProperty subject = xmpMeta.getProperty(null, "dc:subject");

		// works!
		XMPProperty subject = xmpMeta.getProperty("http://purl.org/dc/elements/1.1/", "subject");
		// null
		XMPProperty desc = xmpMeta.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "Description");

		// Yes! 63/10
		XMPProperty fnumber = xmpMeta.getProperty("http://ns.adobe.com/exif/1.0/", "FNumber");
		// speed ratings are a list?
		XMPProperty iso = xmpMeta.getProperty("http://ns.adobe.com/exif/1.0/", "ISOSpeedRatings");

		Iterator<XMPPropertyInfo> isoIter = xmpMeta.iterator("http://ns.adobe.com/exif/1.0/", "ISOSpeedRatings",
				new IteratorOptions().setJustChildren(true));
		isoIter.forEachRemaining((v) -> System.err.println("iso[]: " + v.getValue()));

		Iterator<XMPPropertyInfo> subjectIter = xmpMeta.iterator("http://purl.org/dc/elements/1.1/", "subject",
				new IteratorOptions().setJustChildren(true));
		subjectIter.forEachRemaining((v) -> System.err.println("subject[]: " + v.getValue()));

		// Can just use getProperty for this, but perhaps this is more flexible??
		// nope...
		Iterator<XMPPropertyInfo> fnumberIter = xmpMeta.iterator("http://purl.org/dc/elements/1.1/", "FNumber",
				new IteratorOptions().setJustChildren(true));
		fnumberIter.forEachRemaining((v) -> System.err.println("fnumber[]: " + v.getValue()));
		
		
		// PixelXDimension
		// PixelYDimension
		// exif:FocalLength="4000/10"

		//xmpMeta.iterator().forEachRemaining(System.err::println);
		
		System.err.println();
	}

	//private 
}
