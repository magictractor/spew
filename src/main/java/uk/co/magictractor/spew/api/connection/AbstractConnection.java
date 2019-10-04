package uk.co.magictractor.spew.api.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewServiceProvider;

public abstract class AbstractConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final APP application;

    public AbstractConnection(APP application) {
        this.application = application;
    }

    @Override
    public APP getApplication() {
        return application;
    }

    public SP getServiceProvider() {
        return application.getServiceProvider();
    }

    protected final Logger getLogger() {
        return logger;
    }

}
