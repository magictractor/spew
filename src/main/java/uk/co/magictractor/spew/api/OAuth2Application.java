package uk.co.magictractor.spew.api;

import java.util.function.Supplier;

public interface OAuth2Application extends OAuthApplication { // <OAuth2ServiceProvider> {

    @Override
    OAuth2ServiceProvider getServiceProvider();

    String getClientId();

    String getClientSecret();

    // https://brandur.org/oauth-scope
    String getScope();

    // default?
    OAuth2AuthorizeResponseType defaultAuthorizeResponseType();

    @Override
    default Supplier<OAuthConnection> getNewConnectionSupplier() {
        return () -> new OAuth2Connection(this);
    }

}
