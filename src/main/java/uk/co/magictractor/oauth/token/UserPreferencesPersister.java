package uk.co.magictractor.oauth.token;

import java.util.prefs.Preferences;

import uk.co.magictractor.oauth.api.OAuthServer;

public class UserPreferencesPersister {

	private final Preferences preferences;
	private final String key;

	public UserPreferencesPersister(OAuthServer authServer, String key) {
		preferences = Preferences.userNodeForPackage(authServer.getClass());
		this.key = key;
	}

	public String getValue() {
		return preferences.get(key, null);
	}

	public void setValue(String value) {
		preferences.put(key, value);
	}

}
