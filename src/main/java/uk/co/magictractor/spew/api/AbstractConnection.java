package uk.co.magictractor.spew.api;

public abstract class AbstractConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        implements SpewConnection {

    private final APP application;

    public AbstractConnection(APP application) {
        this.application = application;
    }

    public APP getApplication() {
        return application;
    }

    public SP getServiceProvider() {
        return application.getServiceProvider();
    }

}
