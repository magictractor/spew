package uk.co.magictractor.spew.store;

import java.util.Optional;
import java.util.prefs.Preferences;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class PreferencesProperty implements EditableProperty {

    private final Preferences preferences;
    private final String key;
    private Optional<String> value;

    /**
     * <p>
     * Default visibility because instances should only be created from a
     * property store, and the property store should only be accessed via
     * SPIUtil.
     * </p>
     */
    /* default */ PreferencesProperty(SpewApplication application, String key) {
        // Hmm. regedit shows slashes before capital letters
        // See
        // https://stackoverflow.com/questions/23001152/in-java-why-does-windowspreferences-use-slashes-for-capital-letters
        preferences = Preferences.userNodeForPackage(application.getClass())
                .node(application.getClass().getSimpleName());
        this.key = key;
    }

    @Override
    public String getValue() {
        return getValue(null);
    }

    @Override
    public String getValue(String def) {
        if (value == null) {
            value = Optional.ofNullable(preferences.get(key, def));
        }
        return value.orElse(null);
    }

    @Override
    public void setValue(String value) {
        preferences.put(key, value);
        ExceptionUtil.call(() -> preferences.flush());
        this.value = Optional.ofNullable(value);
    }

    /** Used for temporary token and secret with OAuth1. */
    // TODO! pass params rather than using this??
    @Override
    public void setUnpersistedValue(String value) {
        this.value = Optional.ofNullable(value);
    }

}
