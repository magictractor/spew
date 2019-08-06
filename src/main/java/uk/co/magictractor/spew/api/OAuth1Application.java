package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import uk.co.magictractor.spew.server.OAuth1VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;
import uk.co.magictractor.spew.server.ShutdownOnceVerifiedRequestHandler;
import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

@SpewAuthType
public interface OAuth1Application extends SpewApplication, HasCallbackServer {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    default String getConsumerKey() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_key");
    }

    default String getConsumerSecret() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_secret");
    }

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
