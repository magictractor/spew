package uk.co.magictractor.oauth.api;

import java.util.function.Supplier;

public interface OAuth1Application extends OAuthApplication { // <OAuth1ServiceProvider> {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    String getAppToken();

    String getAppSecret();

    @Override
    default Supplier<OAuthConnection> getNewConnectionSupplier() {
        return () -> new OAuth1Connection(this);
    }

}
