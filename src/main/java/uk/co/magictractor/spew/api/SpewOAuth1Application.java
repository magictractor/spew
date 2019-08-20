package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import uk.co.magictractor.spew.core.verification.VerificationFunction;
import uk.co.magictractor.spew.server.ConnectionValuesRequestHandler;
import uk.co.magictractor.spew.server.OAuth1VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;
import uk.co.magictractor.spew.server.ShutdownOnceVerifiedRequestHandler;
import uk.co.magictractor.spew.server.TemplateRequestHandler;
import uk.co.magictractor.spew.store.ApplicationPropertyStore;
import uk.co.magictractor.spew.util.spi.SPIUtil;

@SpewAuthType
public interface SpewOAuth1Application<SP extends SpewOAuth1ServiceProvider>
        extends SpewApplication<SP>, HasCallbackServer {

    default String getConsumerKey() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_key");
    }

    default String getConsumerSecret() {
        return SPIUtil.firstAvailable(ApplicationPropertyStore.class).getProperty(this, "consumer_secret");
    }

    @Override
    default List<RequestHandler> getServerRequestHandlers(Supplier<VerificationFunction> verificationFunctionSupplier) {
        return Arrays.asList(
            new ConnectionValuesRequestHandler(),
            new OAuth1VerificationRequestHandler(verificationFunctionSupplier),
            new ShutdownOnceVerifiedRequestHandler(),
            new TemplateRequestHandler(serverResourcesRelativeToClass()),
            new ResourceRequestHandler(serverResourcesRelativeToClass()));
    }

}
