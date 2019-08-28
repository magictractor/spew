package uk.co.magictractor.spew.util;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.server.OutgoingStaticResponse;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    public static SpewParsedResponse readResponse(SpewApplication<?> application, Class<?> testClass, String fileName) {
        OutgoingStaticResponse response = new OutgoingStaticResponse(testClass, fileName);
        return SpewParsedResponse.parse(application, response);
    }

}
