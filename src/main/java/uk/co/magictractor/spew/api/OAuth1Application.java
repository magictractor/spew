package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import uk.co.magictractor.spew.server.OAuth1VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;

public interface OAuth1Application extends SpewApplication, HasCallbackServer {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    String getConsumerKey();

    String getConsumerSecret();

    @Override
    default List<RequestHandler> getServerRequestHandlers(BiFunction<String, String, Boolean> verificationFunction) {
        return Arrays.asList(
            new OAuth1VerificationRequestHandler(verificationFunction),
            new ResourceRequestHandler(serverResourcesRelativeToClass()));
    }

}
