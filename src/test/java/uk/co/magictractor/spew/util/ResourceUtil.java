package uk.co.magictractor.spew.util;

import java.nio.file.Path;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponseBuilder;
import uk.co.magictractor.spew.server.OutgoingStaticResponse;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    public static SpewParsedResponse readResponse(SpewApplication<?> application, Class<?> testClass, String fileName) {
        Path bodyPath = PathUtil.regularFile(testClass, fileName);
        OutgoingStaticResponse response = new OutgoingStaticResponse(bodyPath);

        return new SpewParsedResponseBuilder(application, response).build();
    }

}
