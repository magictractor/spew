package uk.co.magictractor.spew.api.connection;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.api.SpewServiceProvider;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 * A connection which uses a different type of connection to transport requests
 * to the service provider requests, but adds authorization to the requests.
 */
public abstract class AbstractAuthorizationDecoratorConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        extends AbstractAuthorizationConnection<APP, SP> {

    private final SpewConnection transport;

    protected AbstractAuthorizationDecoratorConnection(APP application) {
        super(application);
        transport = SPIUtil
                .firstNotNull(SpewConnectionFactory.class, SpewConnectionFactory::createConnectionWithoutAuth)
                .orElseThrow(() -> new IllegalStateException(
                    "There is no available " + SpewConnectionFactory.class.getSimpleName()
                            + " instance which will create a connection with no authorisation"));
    }

    @Override
    protected SpewHttpResponse sendRequest0(OutgoingHttpRequest apiRequest) {
        /*
         * Uses request() rather than sendRequest(), but there should should be
         * no additional callbacks on the transport connection, so that isn't a
         * problem.
         */
        return transport.request(apiRequest);
    }

}
