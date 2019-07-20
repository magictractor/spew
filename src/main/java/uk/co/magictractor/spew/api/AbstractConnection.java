package uk.co.magictractor.spew.api;

public class AbstractConnection<APP extends SpewApplication, SP extends SpewServiceProvider> {

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
