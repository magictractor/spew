package uk.co.magictractor.oauth.processor.properties;

import java.lang.ref.WeakReference;
import java.util.Properties;

public class CachedResourceFileProperties implements ResourceFileProperties {

	private WeakReference<Properties> propertiesRef;

	public Properties getProperties() {
		if (propertiesRef == null) {
			Properties properties = readProperties(getResourceName());
			propertiesRef = new WeakReference<Properties>(properties);
		}
		// Hmm. Is there a (very small) possibility that Ref could have been reclaimed
		// between the null check and here?
		return propertiesRef.get();
	}

}
