package uk.co.magictractor.oauth.local;

import static uk.co.magictractor.oauth.local.ConvertedPhotoPropertiesSupplier.asInteger;
import static uk.co.magictractor.oauth.local.ConvertedPhotoPropertiesSupplier.onlyElement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.adobe.xmp.XMPDateTime;
import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.adobe.xmp.XMPMetaFactory;
import com.adobe.xmp.options.IteratorOptions;
import com.adobe.xmp.properties.XMPProperty;
import com.adobe.xmp.properties.XMPPropertyInfo;
import com.google.common.collect.Streams;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.util.ExceptionUtil;

/**
 * Photo properties read from a sidecar (.xmp) file.
 * 
 * MetadataExtractor does not currently support reading values from sidecar
 * files.
 * 
 * Use Adobe's XMPMeta to read sidecar data, as suggested in
 * https://github.com/drewnoakes/metadata-extractor/issues/118.
 */
public class SidecarPropertiesSupplierFactory implements PhotoPropertiesSupplierFactory {

	private static final Namespace XMP = new Namespace("xmp", "http://ns.adobe.com/xap/1.0/");
	private static final Namespace DC = new Namespace("dc", "http://purl.org/dc/elements/1.1/");
	private static final Namespace EXIF = new Namespace("exif", "http://ns.adobe.com/exif/1.0/");

	private final Path path;
	private XMPMeta xmpMeta;

	public SidecarPropertiesSupplierFactory(Path path) {
		this.path = path;
		ExceptionUtil.call(this::init);
	}

	private void init() throws IOException, XMPException {
		try (InputStream sidecarStream = Files.newInputStream(path)) {
			xmpMeta = XMPMetaFactory.parse(sidecarStream);
		}
	}

	@Override
	public Stream<PhotoPropertiesSupplier<String>> getFileNamePropertyValueSuppliers() {
		// The file name is always taken from the image, not the sidecar.
		return Stream.empty();
	}

	@Override
	public Stream<PhotoPropertiesSupplier<String>> getTitlePropertyValueSuppliers() {
		return Stream.of(createLocalizedTextSupplier(DC, "title"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<String>> getDescriptionPropertyValueSuppliers() {
		return Stream.of(createLocalizedTextSupplier(DC, "description"),
				createLocalizedTextSupplier(EXIF, "UserComment"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<TagSet>> getTagSetPropertyValueSuppliers() {
		// TODO implement this
		return null;
	}

	@Override
	public Stream<PhotoPropertiesSupplier<Instant>> getDateTimeTakenPropertyValueSuppliers() {
		return Stream.of(
				// createInstantSupplier(EXIF, "DateTimeOriginal"),
				createInstantSupplier(XMP, "CreateDate"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<Integer>> getRatingPropertyValueSuppliers() {
		return Stream.of(createIntegerSupplier(XMP, "Rating"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<String>> getShutterSpeedPropertyValueSuppliers() {
		return Stream.of(createStringSupplier(EXIF, "ExposureTime"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<String>> getAperturePropertyValueSuppliers() {
		return Stream.of(createStringSupplier(EXIF, "FNumber"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<Integer>> getIsoPropertyValueSuppliers() {
		return Stream.of(asInteger(onlyElement(createListSupplier(EXIF, "ISOSpeedRatings"))));
	}

	// and exif:ExposureBiasValue

	@Override
	public Stream<PhotoPropertiesSupplier<Integer>> getWidthValueSuppliers() {
		return Stream.of(createIntegerSupplier(EXIF, "PixelXDimension"));
	}

	@Override
	public Stream<PhotoPropertiesSupplier<Integer>> getHeightValueSuppliers() {
		return Stream.of(createIntegerSupplier(EXIF, "PixelYDimension"));
	}

	private XmpMetaPropertyValueSupplier<String> createStringSupplier(Namespace namespace, String propertyName) {
		return new XmpMetaPropertyValueSupplier<>(namespace, propertyName, (n, p) -> xmpMeta.getPropertyString(n, p));
	}

	private XmpMetaPropertyValueSupplier<Integer> createIntegerSupplier(Namespace namespace, String propertyName) {
		return new XmpMetaPropertyValueSupplier<>(namespace, propertyName, (n, p) -> xmpMeta.getPropertyInteger(n, p));
	}

	private XmpMetaPropertyValueSupplier<Instant> createInstantSupplier(Namespace namespace, String propertyName) {
		return new XmpMetaPropertyValueSupplier<>(namespace, propertyName, this::getInstant);
	}

	private Instant getInstant(String namespaceUri, String propertyName) throws XMPException {
		XMPDateTime dt = xmpMeta.getPropertyDate(namespaceUri, propertyName);
		if (dt == null) {
			return null;
		}

		// TODO! find and cite source which verifies this is always UTC
		return ZonedDateTime.of(dt.getYear(), dt.getMonth(), dt.getDay(), dt.getHour(), dt.getMinute(), dt.getSecond(),
				dt.getNanoSecond(), ZoneOffset.UTC).toInstant();
	}

	private XmpMetaPropertyValueSupplier<List<String>> createListSupplier(Namespace namespace, String propertyName) {
		return new XmpMetaPropertyValueSupplier<>(namespace, propertyName, this::getList);
	}

	private List<String> getList(String namespaceUri, String propertyName) throws XMPException {
		@SuppressWarnings("unchecked")
		Iterator<XMPPropertyInfo> iter = xmpMeta.iterator(namespaceUri, propertyName,
				new IteratorOptions().setJustChildren(true));
		return Streams.stream(iter).map(XMPPropertyInfo::getValue).collect(Collectors.toList());
	}

	// Title, description etc may have multiple values for different locales.
	private XmpMetaPropertyValueSupplier<String> createLocalizedTextSupplier(Namespace namespace, String propertyName) {
		return new XmpMetaPropertyValueSupplier<>(namespace, propertyName, this::getLocalizedText);
	}

	private String getLocalizedText(String namespaceUri, String propertyName) throws XMPException {
		XMPProperty localizedText = xmpMeta.getLocalizedText(namespaceUri, propertyName, null, "x-default");
		return localizedText == null ? null : localizedText.getValue();
	}

	private class XmpMetaPropertyValueSupplier<T> implements PhotoPropertiesSupplier<T> {

		protected final Namespace namespace;
		protected final String propertyName;
		protected final XmpMetaGetter<T> getter;

		protected XmpMetaPropertyValueSupplier(Namespace namespace, String propertyName, XmpMetaGetter<T> getter) {
			this.namespace = namespace;
			this.propertyName = propertyName;
			this.getter = getter;
		}

		@Override
		public T get() {
			return ExceptionUtil.call(() -> getter.get(namespace.uri, propertyName));
		}

		@Override
		public final String getDescription() {
			return namespace.prefix + ":" + propertyName;
		}
	}

	@FunctionalInterface
	private static interface XmpMetaGetter<T> {
		T get(String namespaceUri, String propertyName) throws XMPException;
	}

	private static class Namespace {
		private String prefix;
		private String uri;

		Namespace(String prefix, String uri) {
			this.prefix = prefix;
			this.uri = uri;
		}
	}

}
