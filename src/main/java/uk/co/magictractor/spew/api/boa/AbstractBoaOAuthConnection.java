package uk.co.magictractor.spew.api.boa;

import java.io.IOException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.AbstractConnection;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewHttpUrlConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;

// Common code for OAuth1 and OAuth2 implementations.
public abstract class AbstractBoaOAuthConnection<APP extends SpewApplication, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    // TODO! from factory and abstract class
    private final SpewHttpUrlConnection<APP, SP> transport;

    protected AbstractBoaOAuthConnection(APP application) {
        super(application);
        transport = new SpewHttpUrlConnection<>(application);
    }

    protected final Logger getLogger() {
        return logger;
    }

    protected final SpewResponse request0(SpewRequest request) throws IOException {
        return request0(request, null);
    }

    // http://www.baeldung.com/java-http-request
    protected final SpewResponse request0(SpewRequest request, Consumer<SpewRequest> initConnection)
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
