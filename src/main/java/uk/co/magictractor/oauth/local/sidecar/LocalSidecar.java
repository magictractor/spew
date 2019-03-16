package uk.co.magictractor.oauth.local.sidecar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.properties.XMPProperty;
import com.adobe.xmp.properties.XMPPropertyInfo;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.local.BaseLocalPhoto;
import uk.co.magictractor.oauth.util.ExceptionUtil;

/**
 * MetadataExtractor does not currently support reading values from sidecar
 * files.
 * 
 * Use Adobe's XMPMeta to read sidecar data, as suggested in
 * https://github.com/drewnoakes/metadata-extractor/issues/118.
 */
public class LocalSidecar extends BaseLocalPhoto {

	private static final String XMP = "http://ns.adobe.com/xap/1.0/";
	private static final String DC = "http://purl.org/dc/elements/1.1/";
	private static final String EXIF = "http://ns.adobe.com/exif/1.0/";

	// Only non-null while properties are initialised, to reduce memory footprint.
	private XMPMeta xmpMeta;

	public LocalSidecar(Path path) {
		super(path);
	}

	public void preInit() {
		ExceptionUtil.call(this::preInit0);
	}

	private void preInit0() throws IOException, XMPException {
		try (InputStream sidecarStream = Files.newInputStream(getPath())) {
			xmpMeta = XMPMetaFactory.parse(sidecarStream);
		}
	}

	public void postInit() {
		xmpMeta = null;
	}

	@Override
	protected List<SupplierWithDescription<String>> getTitlePropertyValueSuppliers() {
		return Arrays.asList(new XmpMetaLocalizedTextPropertyValueSupplier(DC, "title"));
	}

	@Override
	protected List<SupplierWithDescription<String>> getDescriptionPropertyValueSuppliers() {
		return Arrays.asList(new XmpMetaLocalizedTextPropertyValueSupplier(DC, "description"),
				new XmpMetaLocalizedTextPropertyValueSupplier(EXIF, "UserComment"));
	}

	@Override
	protected List<SupplierWithDescription<TagSet>> getTagSetPropertyValueSuppliers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<SupplierWithDescription<Instant>> getDateTimeTakenPropertyValueSuppliers() {
		return Collections.emptyList();
	}

	@Override
	protected List<SupplierWithDescription<Integer>> getRatingPropertyValueSuppliers() {
		return Arrays.asList(new XmpMetaIntegerPropertyValueSupplier(XMP, "Rating"));
	}

	private abstract class XmpMetaPropertyValueSupplier<T> implements SupplierWithDescription<T> {

		protected final String nameSpace;
		protected final String propertyName;

		protected XmpMetaPropertyValueSupplier(String nameSpace, String propertyName) {
			this.nameSpace = nameSpace;
			this.propertyName = propertyName;
		}

		@Override
		public T get() {
			return ExceptionUtil.call(this::get0);
		}

		protected abstract T get0() throws XMPException;

		@Override
		public final String getDescription() {
			return "XMP meta property " + propertyName + " in namespace " + nameSpace;
		}
	}

	private class XmpMetaLocalizedTextPropertyValueSupplier extends XmpMetaPropertyValueSupplier<String> {

		protected XmpMetaLocalizedTextPropertyValueSupplier(String nameSpace, String propertyName) {
			super(nameSpace, propertyName);
		}

		@Override
		public String get0() throws XMPException {
			// Title, description etc may have multiple values for different locales.
			XMPProperty localizedText = xmpMeta.getLocalizedText(nameSpace, propertyName, null, "x-default");
			return localizedText == null ? null : localizedText.getValue();
		}
	}

	private class XmpMetaIntegerPropertyValueSupplier extends XmpMetaPropertyValueSupplier<Integer> {

		protected XmpMetaIntegerPropertyValueSupplier(String nameSpace, String propertyName) {
			super(nameSpace, propertyName);
		}

		@Override
		public Integer get0() throws XMPException {
//			Iterator<XMPPropertyInfo> iter = xmpMeta.iterator(nameSpace, propertyName, new IteratorOptions().setJustChildren(true));
//			iter.forEachRemaining((v) -> System.err.println("iter: " + v.getValue()));

			return xmpMeta.getPropertyInteger(nameSpace, propertyName);
		}
	}

//	private class XmpMetaInstantPropertyValueSupplier extends XmpMetaPropertyValueSupplier<Instant> {
//
//		protected XmpMetaInstantPropertyValueSupplier(String nameSpace, String propertyName) {
//			super(nameSpace, propertyName);
//		}
//
//		@Override
//		public Instant get0() throws XMPException {
////			Iterator<XMPPropertyInfo> iter = xmpMeta.iterator(nameSpace, propertyName, new IteratorOptions().setJustChildren(true));
////			iter.forEachRemaining((v) -> System.err.println("iter: " + v.getValue()));
//			
//			XMPDateTime dateTime = xmpMeta.getPropertyDate(nameSpace, propertyName);
//			return Instant.ofEpochSecond(dateTime.getSecond(), dateTime.getNanoSecond());
//		}
//	}

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

		// xmpMeta.iterator().forEachRemaining(System.err::println);

		System.err.println();
	}

}
