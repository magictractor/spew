package uk.co.magictractor.spew.api;

public interface OAuthConnection { // <APP extends OAuthApplication, SP extends ServiceProvider> {

    OAuthResponse request(OAuthRequest apiRequest);

    //OAuthApplication getApplication();

    //
    //	// ?
    //	default OAuthServiceProvider getServiceProvider() {
    //		return getApplication().getServiceProvider();
    //	}

}
