package uk.co.magictractor.spew.api;

import java.util.function.Supplier;

public interface OAuthApplication { // <SP extends OAuthServiceProvider> {

    // SP getServiceProvider();
    OAuthServiceProvider getServiceProvider();

    Supplier<OAuthConnection> getNewConnectionSupplier();

}
