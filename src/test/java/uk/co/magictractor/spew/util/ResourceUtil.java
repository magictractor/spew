package uk.co.magictractor.spew.util;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.ResourceResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    public static SpewParsedResponse readResponse(SpewApplication<?> application, Class<?> testClass, String fileName) {
        ResourceResponse response = new ResourceResponse(testClass, fileName);
        return SpewParsedResponse.parse(application, response);
    }

}
