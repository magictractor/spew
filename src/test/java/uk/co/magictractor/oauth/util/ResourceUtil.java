package uk.co.magictractor.oauth.util;

import java.io.InputStream;

import uk.co.magictractor.oauth.api.OAuthJsonResponse;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.google.Google;

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
	private static OAuthResponse buildResponse(Class<?> testClass, String fileName) {
		InputStream in = testClass.getResourceAsStream(fileName);
		if (in == null) {
			throw new IllegalStateException("resource not found: " + testClass.getResource(fileName));
		}

		String json = IOUtil.readStringAndClose(in);
		return new OAuthJsonResponse(json, Google.getInstance().getJsonConfiguration());
	}

}