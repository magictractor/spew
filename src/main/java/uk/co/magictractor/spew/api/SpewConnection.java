package uk.co.magictractor.spew.api;

public interface SpewConnection { // <APP extends OAuthApplication, SP extends ServiceProvider> {

    SpewResponse request(SpewRequest apiRequest);

    //OAuthApplication getApplication();

    //
    //	// ?
    //	default OAuthServiceProvider getServiceProvider() {
    //		return getApplication().getServiceProvider();
    //	}

}
