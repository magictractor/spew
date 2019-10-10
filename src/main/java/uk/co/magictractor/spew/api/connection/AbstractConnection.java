package uk.co.magictractor.spew.api.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewServiceProvider;

public abstract class AbstractConnection<APP extends SpewApplication<SP>, SP extends SpewServiceProvider>
        implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected final Logger getLogger() {
        return logger;
    }

}
