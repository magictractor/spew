package uk.co.magictractor.spew.api.connection;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewConnectionConfiguration;
import uk.co.magictractor.spew.util.RandomUtil;

public abstract class AbstractSpewConnection<CONFIG extends SpewConnectionConfiguration>
        implements SpewConnection {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final CONFIG configuration;

    /**
     * Connection ids are hex values so that they are less likely to be confused
     * with application ids.
     */
    private final String id = RandomUtil.nextHexLong();

    protected AbstractSpewConnection(CONFIG configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must not be null");
        }
        this.configuration = configuration;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public CONFIG getConfiguration() {
        return configuration;
    }

    protected final Logger getLogger() {
        return logger;
    }

    @Override
    public void addProperties(Map<String, Object> properties) {
        configuration.addProperties(properties);
    }

}
