package uk.co.magictractor.spew.api.boa;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.AbstractConnection;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;
import uk.co.magictractor.spew.connection.ConnectionRequest;
import uk.co.magictractor.spew.connection.ConnectionRequestFactory;
import uk.co.magictractor.spew.core.response.HttpUrlConnectionResponse;

// Common code for OAuth1 and OAuth2 implementations.
public abstract class AbstractBoaOAuthConnection<APP extends SpewApplication, SP extends SpewServiceProvider>
        extends AbstractConnection<APP, SP> implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected AbstractBoaOAuthConnection(APP application) {
        super(application);
    }

    protected final Logger getLogger() {
        return logger;
    }

    protected final SpewResponse request0(SpewRequest request) throws IOException {
        return request0(request, null);
    }

    // http://www.baeldung.com/java-http-request
    protected final SpewResponse request0(SpewRequest request, Consumer<HttpURLConnection> initConnection)
            throws IOException {

        URL url = new URL(getUrl(request));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(request.getHttpMethod());

        // Used to set authentication in headers for OAuth2
        if (initConnection != null) {
            initConnection.accept(con);
        }

        ConnectionRequest connectionRequest = ConnectionRequestFactory.createConnectionRequest(con);
        // TODO! rework jsonConfiguration here
        connectionRequest.writeParams(request, getServiceProvider().getJsonConfiguration());

        return new HttpUrlConnectionResponse(con);
    }

    protected String getUrl(SpewRequest request) {
        return request.getUrl(true);
    }

}
