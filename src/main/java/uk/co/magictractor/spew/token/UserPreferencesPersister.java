package uk.co.magictractor.spew.token;

import java.util.Optional;
import java.util.prefs.Preferences;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class UserPreferencesPersister {

    private final Preferences preferences;
    private final String key;
    private Optional<String> value;

    public UserPreferencesPersister(SpewApplication application, String key) {
        // Hmm. regedit shows slashes before capital letters
        // See
        // https://stackoverflow.com/questions/23001152/in-java-why-does-windowspreferences-use-slashes-for-capital-letters
        preferences = Preferences.userNodeForPackage(application.getClass())
                .node(application.getClass().getSimpleName());
        this.key = key;
    }

    public String getValue() {
        return getValue(null);
    }

    public String getValue(String def) {
        if (value == null) {
            value = Optional.ofNullable(preferences.get(key, def));
        }
        return value.orElse(null);
    }

    public void setValue(String value) {
        preferences.put(key, value);
        ExceptionUtil.call(() -> preferences.flush());
        this.value = Optional.ofNullable(value);
    }

    /** Used for temporary token and secret with OAuth1. */
    // TODO! pass params rather than using this??
    public void setUnpersistedValue(String value) {
        this.value = Optional.ofNullable(value);
    }

}
