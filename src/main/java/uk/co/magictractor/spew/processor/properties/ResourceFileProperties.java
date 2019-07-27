package uk.co.magictractor.spew.processor.properties;

import java.io.InputStream;
import java.util.Properties;

import uk.co.magictractor.spew.util.IOUtil;

public class ResourceFileProperties {

    private final Class<?> clazz;
    //private WeakReference<Properties> propertiesRef;
    private Properties properties;

    // TODO! default should be null
    private String resourceFolder = "oauth";

    // TODO! bin this constructor?
    public ResourceFileProperties(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ResourceFileProperties(Object obj) {
        clazz = obj.getClass();
    }

    protected String getResourceName() {
        // TODO! and dir
        StringBuilder resourceNameBuilder = new StringBuilder();

        if (resourceFolder != null) {
            // leading slash??
            resourceNameBuilder.append('/');
            resourceNameBuilder.append(resourceFolder);
            resourceNameBuilder.append('/');
        }

        resourceNameBuilder.append(clazz.getSimpleName().toLowerCase());
        resourceNameBuilder.append(".properties");

        return resourceNameBuilder.toString();
    }

    // TODO! is "folder" the correct terminology
    //	private String getResourceFolder() {
    //		return null;
    //	}

    public String getProperty(String key) {
        String value = getProperties().getProperty(key);
        if (value == null) {
            throw new IllegalStateException("Missing property for key " + key + " in resource " + getResourceName());
        }
        return value;
    }

    private Properties getProperties() {
        if (properties == null) {
            properties = readProperties();
        }
        return properties;
    }

    //    private Properties getProperties() {
    //        if (propertiesRef == null) {
    //            Properties properties = readProperties();
    //            propertiesRef = new WeakReference<Properties>(properties);
    //        }
    //        // Hmm. Is there a (very small) possibility that Ref could have been reclaimed
    //        // between the null check and here? Have seen one mystery NPE - perhaps from this.
    //        return propertiesRef.get();
    //    }

    private Properties readProperties() {
        Properties properties = new Properties();

        String resourceName = getResourceName();
        InputStream resourceStream = clazz.getResourceAsStream(resourceName);
        if (resourceStream == null) {
            throw new IllegalStateException(buildMissingResourceMessage(resourceName));
        }

        IOUtil.acceptThenClose(resourceStream, res -> properties.load(res));

        return properties;
    }

    protected String buildMissingResourceMessage(String resourceName) {
        return "Missing resouce file: " + resourceName;
    }

}
