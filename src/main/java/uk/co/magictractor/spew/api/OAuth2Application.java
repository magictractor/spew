package uk.co.magictractor.spew.api;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.server.OAuth2VerificationRequestHandler;
import uk.co.magictractor.spew.server.RequestHandler;
import uk.co.magictractor.spew.server.ResourceRequestHandler;

public interface OAuth2Application extends SpewApplication, HasCallbackServer {

    @Override
    OAuth2ServiceProvider getServiceProvider();

    String getClientId();

    String getClientSecret();

    // https://brandur.org/oauth-scope
    // https://tools.ietf.org/html/rfc6749#section-3.3
    default String getScope() {
        return null;
    }

    @Override
    default List<RequestHandler> getServerRequestHandlers(VerificationFunction verificationFunction) {
        return Arrays.asList(
            new OAuth2VerificationRequestHandler(verificationFunction),
            new ResourceRequestHandler(serverResourcesRelativeToClass()));
    }

}
