package uk.co.magictractor.oauth.token;

import java.util.prefs.Preferences;

import uk.co.magictractor.oauth.api.OAuthApplication;

public class UserPreferencesPersister {

	private final Preferences preferences;
	private final String key;

	public UserPreferencesPersister(OAuthApplication application, String key) {
		// Hmm. regedit shows slashes before capital letters
		// See https://stackoverflow.com/questions/23001152/in-java-why-does-windowspreferences-use-slashes-for-capital-letters
		preferences = Preferences.userNodeForPackage(application.getClass()).node(application.getClass().getSimpleName());
		this.key = key;
	}

	public String getValue() {
		return preferences.get(key, null);
	}

	public void setValue(String value) {
		preferences.put(key, value);
	}

}
