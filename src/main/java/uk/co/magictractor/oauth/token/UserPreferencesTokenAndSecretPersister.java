package uk.co.magictractor.oauth.token;

import java.util.prefs.Preferences;

import uk.co.magictractor.oauth.api.OAuthServer;

public class UserPreferencesTokenAndSecretPersister implements TokenAndSecretPersister {

	private static final String KEY = "tokenAndSecret";

	private final Preferences preferences;

	public UserPreferencesTokenAndSecretPersister(OAuthServer authServer) {
		preferences = Preferences.userNodeForPackage(authServer.getClass());
	}

	@Override
	public String getTokenAndSecretString() {
		return preferences.get(KEY, null);
	}

	@Override
	public void setTokenAndSecretString(String tokenAndSecretString) {
		preferences.put(KEY, tokenAndSecretString);
	}

}
