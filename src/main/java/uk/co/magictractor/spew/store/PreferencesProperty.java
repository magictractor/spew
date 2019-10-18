package uk.co.magictractor.spew.store;

import java.util.prefs.Preferences;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class PreferencesProperty extends AbstractEditableProperty {

    private final Preferences preferences;

    /**
     * <p>
     * Instances should generally be created from a property store, and the
     * property store should generally be accessed only via SPIUtil.
     * </p>
     */
    public PreferencesProperty(SpewApplication<?> application, String key) {
        super(application, key);
        // Hmm. regedit shows slashes before capital letters
        // See
        // https://stackoverflow.com/questions/23001152/in-java-why-does-windowspreferences-use-slashes-for-capital-letters
        preferences = Preferences.userNodeForPackage(application.getClass())
                .node(application.getClass().getSimpleName());
    }

    @Override
    public String fetchValue(String key, String def) {
        return preferences.get(key, def);
    }

    @Override
    public void persistValue(String key, String value) {
        preferences.put(key, value);
        ExceptionUtil.call(() -> preferences.flush());
    }

}
