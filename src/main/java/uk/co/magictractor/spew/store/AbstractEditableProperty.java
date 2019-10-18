package uk.co.magictractor.spew.store;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.spew.api.SpewApplication;

public abstract class AbstractEditableProperty implements EditableProperty {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String key;
    private Optional<String> value;

    /**
     * <p>
     * Instances should generally be created from a property store, and the
     * property store should generally be accessed only via SPIUtil.
     * </p>
     */
    public AbstractEditableProperty(SpewApplication<?> application, String key) {
        this.key = key;
    }

    @Override
    public final String getValue() {
        return getValue(null);
    }

    @Override
    public final String getValue(String def) {
        if (value == null) {
            value = Optional.ofNullable(fetchValue(key, def));
        }
        return value.orElse(null);
    }

    @Override
    public final void setValue(String value) {
        persistValue(key, value);
        this.value = Optional.ofNullable(value);
    }

    public abstract String fetchValue(String key, String def);

    public abstract void persistValue(String key, String value);

    protected final Logger getLogger() {
        return logger;
    }

}
