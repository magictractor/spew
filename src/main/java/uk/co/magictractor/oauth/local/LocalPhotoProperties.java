package uk.co.magictractor.oauth.local;

/**
 * Holder for values gathered from Exif etc in Raw or Jpeg files and also from
 * sidecar files.
 * 
 * Multiple sources are checked. Where a value (such as title) is found in
 * multiple locations, it should ideally be the same in all locations. If it is
 * not, a warning is logged (TODO! log the warning), and the first non-empty
 * value encountered wins.
 */
public class LocalPhotoProperties {

	// One type for each of the fields in LocalPhoto, which will be built from these
	// properties.
	public enum Type {
		Title, Description
	}

	// hmm, what about arrays? iso/subject
	public class PropertyKey {
		//private String value;
		private Type type;
		private Source source;
		// Photo or sidecar
		// sidecar: namespace and key
		// photo
	}

	public interface Source {
	}

	public class PhotoSource implements Source {
		String directoryName;
		String tagName;
	}

	public class SidecarSource implements Source {
		String schemaNameSpace;
		String propertyName;
	}

	public void add(Type type, String value, Source source) {
	}
}
