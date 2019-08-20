package uk.co.magictractor.spew.oauth.boa;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.AbstractConnection;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;
import uk.co.magictractor.spew.api.connection.SpewConnectionFactory;
import uk.co.magictractor.spew.util.spi.SPIUtil;

// Common code for OAuth1 and OAuth2 implementations.
public abstract class AbstractBoaOAuthConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final SpewConnection transport;

    protected AbstractBoaOAuthConnection(APP application) {
        super(application);
        transport = SPIUtil
                .firstNotNull(SpewConnectionFactory.class, SpewConnectionFactory::createConnectionWithoutAuth)
                .orElseThrow(() -> new IllegalStateException(
                    "There is no available " + SpewConnectionFactory.class.getSimpleName()
                            + " instance which will create a connection with no authorisation"));
    }

    protected final Logger getLogger() {
        return logger;
    }

    protected final SpewHttpResponse request0(OutgoingHttpRequest request) throws IOException {
        return request0(request, null);
    }

    // http://www.baeldung.com/java-http-request
    protected final SpewHttpResponse request0(OutgoingHttpRequest request, Consumer<OutgoingHttpRequest> initConnection)
            throws IOException {

        // Used to set authentication in headers for OAuth2, and outh_signature query param for OAuth1
        if (initConnection != null) {
            initConnection.accept(request);
        }

        try {
            return transport.request(request);
        }
        catch (Exception e) {
            System.err.println("broken request: " + request);
            throw e;
        }

    }

}
