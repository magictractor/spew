package uk.co.magictractor.oauth.api;

public class AbstractConnection<APP extends OAuthApplication, SP extends OAuthServiceProvider> {

    private final APP application;

    public AbstractConnection(APP application) {
        this.application = application;
    }

    public APP getApplication() {
        return application;
    }

    public SP getServiceProvider() {
        // TODO! generic or move this?
        return (SP) application.getServiceProvider();
    }

}
