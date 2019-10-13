package uk.co.magictractor.spew.api.connection;

import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.api.SpewHttpResponse;
import uk.co.magictractor.spew.util.spi.SPIUtil;

/**
 * A connection which uses a different type of connection to transport requests
 * to the service provider requests, but adds authorization to the requests.
 */
public abstract class AbstractAuthorizationDecoratorConnection<CONFIG extends SpewConnectionConfiguration>
        extends AbstractAuthorizationConnection<CONFIG> {

    private final SpewConnection transport;

    protected AbstractAuthorizationDecoratorConnection(CONFIG configuration) {
        super(configuration);
        transport = SPIUtil
                .firstNotNull(SpewConnectionFactory.class,
                    factory -> factory.createConnectionWithoutAuth(configuration))
                .orElseThrow(() -> new IllegalStateException(
                    "There is no available " + SpewConnectionFactory.class.getSimpleName()
                            + " instance which will create a connection with no authorisation"));
    }

    @Override
    public SpewHttpResponse request(OutgoingHttpRequest request) {
        return transport.request(request);
    }

}
