package uk.co.magictractor.spew.api;

import java.util.function.Supplier;

public interface OAuth1Application extends OAuthApplication { // <OAuth1ServiceProvider> {

    @Override
    OAuth1ServiceProvider getServiceProvider();

    // Spring social calls this consumerKey
    String getAppToken();

    // Spring social calls this consumerSecret
    String getAppSecret();

    @Override
    default Supplier<SpewConnection> getNewConnectionSupplier() {
        //
        return () -> new OAuth1Connection(this);
    }

}
