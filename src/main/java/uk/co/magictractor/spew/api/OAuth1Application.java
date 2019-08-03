package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import uk.co.magictractor.spew.server.OAuth1VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;
import uk.co.magictractor.spew.server.ShutdownOnceVerifiedRequestHandler;

@SpewAuthType
public interface OAuth1Application extends SpewApplication, HasCallbackServer {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    String getConsumerKey();

    String getConsumerSecret();

    @Override
    default String getOutOfBandUri() {
        return "oob";
    }

    @Override
    default List<RequestHandler> getServerRequestHandlers(Supplier<VerificationFunction> verificationFunctionSupplier) {
        return Arrays.asList(
            new OAuth1VerificationRequestHandler(verificationFunctionSupplier),
            new ShutdownOnceVerifiedRequestHandler(),
            new ResourceRequestHandler(serverResourcesRelativeToClass()));
    }

}
