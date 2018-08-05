package uk.co.magictractor.oauth.token;

public interface TokenAndSecretPersister {

	default TokenAndSecret getTokenAndSecret() {
		String tokenAndSecretString = getTokenAndSecretString();
		if (tokenAndSecretString == null) {
			return TokenAndSecret.BLANK;
		}
		return new TokenAndSecret(getTokenAndSecretString());
	}

	String getTokenAndSecretString();

	default void setTokenAndSecret(TokenAndSecret tokenAndSecret) {
		setTokenAndSecretString(tokenAndSecret.getPersistedString());
	}

	void setTokenAndSecretString(String tokenAndSecretString);

}
