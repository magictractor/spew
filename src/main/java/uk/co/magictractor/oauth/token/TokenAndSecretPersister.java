package uk.co.magictractor.oauth.token;

public interface TokenAndSecretPersister {

	TokenAndSecret getTokenAndSecret();

	default void setTokenAndSecret(TokenAndSecret tokenAndSecret) {
		throw new UnsupportedOperationException();
	}

}
