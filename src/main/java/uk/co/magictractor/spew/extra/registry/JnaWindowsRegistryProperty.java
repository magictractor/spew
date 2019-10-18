package uk.co.magictractor.spew.extra.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.store.AbstractEditableProperty;

public class JnaWindowsRegistryProperty extends AbstractEditableProperty {

    private final String keyPath;
    private Boolean valueExists;

    /**
     * <p>
     * Instances should generally be created from a property store, and the
     * property store should generally be accessed only via SPIUtil.
     * </p>
     */
    public JnaWindowsRegistryProperty(SpewApplication<?> application, String key) {
        super(application, key);
        keyPath = "Software\\JavaSoft\\Prefs\\" + application.getClass().getName().replace('.', '\\');
    }

    @Override
    public String fetchValue(String key, String def) {
        if (valueExists == null) {
            valueExists = Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, keyPath, key);
        }
        if (!valueExists) {
            getLogger().debug("No registry value for key {}, using default value {}", key, def);
            return def;
        }

        String fetched = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, keyPath, key);
        getLogger().debug("Registry value fetched for key {}, value {}", key, fetched);

        return fetched;
    }

    @Override
    public void persistValue(String key, String value) {
        // keyPathExists may be null here, since fetching one of a set of keys may be sufficient
        // to determine that values are missing, e.g. OAuth secret and token only requires checking
        // whether one of them exists
        if (!Boolean.TRUE.equals(valueExists)) {
            Advapi32Util.registryCreateKey(WinReg.HKEY_CURRENT_USER, keyPath, key);
            valueExists = true;
        }
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, keyPath, key, value);
    }

    // And support remove? Ideally remove path if removing last node too...

}
