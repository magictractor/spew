package uk.co.magictractor.oauth.processor.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

import uk.co.magictractor.oauth.util.IOUtil;

public interface ResourceFileProperties {

	default String getResourceName() {
		// TODO! and dir
		StringBuilder resourceNameBuilder = new StringBuilder();

		String resourceFolder = getResourceFolder();
		if (resourceFolder != null) {
			// leading slash??
			resourceNameBuilder.append('/');
			resourceNameBuilder.append(resourceFolder);
			resourceNameBuilder.append('/');
		}

		resourceNameBuilder.append(getClass().getSimpleName().toLowerCase());
		resourceNameBuilder.append(".properties");

		return resourceNameBuilder.toString();
	}

	// TODO! is "folder" the correct terminology
	default String getResourceFolder() {
		return null;
	}

	default String getProperty(String key) {
		String value = getProperties().getProperty(key);
		if (value == null) {
			throw new IllegalStateException("Missing property for key " + key + " in resource " + getResourceName());
		}
		return value;
	}
	
	default Properties getProperties() {
		return readProperties(getResourceName());
	}

	default Properties readProperties(String resourceName) {
		Properties properties = new Properties();

		InputStream resourceStream = getClass().getResourceAsStream(resourceName);
		if (resourceStream == null) {
			throw new IllegalStateException(buildMissingResourceMessage(resourceName));
		}

		try {
			properties.load(resourceStream);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} finally {
			IOUtil.closeQuietly(resourceStream);
		}

		return properties;
	}

	default String buildMissingResourceMessage(String resourceName) {
		return "Missing resouce file: " + resourceName;
	}

}
