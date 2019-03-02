package uk.co.magictractor.oauth.token;

import java.util.prefs.Preferences;

import uk.co.magictractor.oauth.api.OAuthServiceProvider;

// TODO! generalise a multi value persister?
public class UserPreferencesTokenAndSecretPersister implements TokenAndSecretPersister {

	private static final String KEY = "tokenAndSecret";

	private final Preferences preferences;

	public UserPreferencesTokenAndSecretPersister(OAuthServiceProvider authServer) {
		preferences = Preferences.userNodeForPackage(authServer.getClass());
	}

	@Override
	public TokenAndSecret getTokenAndSecret() {
		return TokenAndSecret.forPersistedString(preferences.get(KEY, null));
	}

	@Override
	public void setTokenAndSecret(TokenAndSecret tokenAndSecret) {
		preferences.put(KEY, tokenAndSecret.getPersistedString());
	}

}
