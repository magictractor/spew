package uk.co.magictractor.spew.api;

// TODO! rename to Spew (could use other type of auth)
public interface OAuthApplication { // <SP extends OAuthServiceProvider> {

    // SP getServiceProvider();
    OAuthServiceProvider getServiceProvider();

}
