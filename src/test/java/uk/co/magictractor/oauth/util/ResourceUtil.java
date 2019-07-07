package uk.co.magictractor.oauth.util;

import java.io.InputStream;

import uk.co.magictractor.spew.api.OAuthJsonResponse;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.google.Google;
import uk.co.magictractor.spew.util.IOUtil;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    // TODO! replace with code from Guava?
    public static String readResource(Class<?> testClass, String fileName) {
        InputStream in = testClass.getResourceAsStream(fileName);
        if (in == null) {
            throw new IllegalStateException("resource not found: " + testClass.getResource(fileName));
        }

        return IOUtil.readStringAndClose(in);
    }

    // TODO! bin or pass in Json Config or extract config from class?
    private static SpewResponse buildResponse(Class<?> testClass, String fileName) {
        InputStream in = testClass.getResourceAsStream(fileName);
        if (in == null) {
            throw new IllegalStateException("resource not found: " + testClass.getResource(fileName));
        }

        String json = IOUtil.readStringAndClose(in);
        return new OAuthJsonResponse(json, Google.getInstance().getJsonConfiguration());
    }

}
