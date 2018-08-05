package uk.co.magictractor.oauth.token;

public class TokenAndSecret {

	public static final TokenAndSecret BLANK = new TokenAndSecret("", "");

	private static final String PERSISTED_VALUE_SEPARATOR = ":";

	private final String token;
	private final String secret;

	public TokenAndSecret(String token, String secret) {
		this.token = token;
		this.secret = secret;
	}

	public static TokenAndSecret forPersistedString(String persistedString) {
		if (persistedString == null) {
			return BLANK;
		}
		
		String[] parts = persistedString.split(PERSISTED_VALUE_SEPARATOR);
		return new TokenAndSecret( parts[0], parts[1]);
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

	public String getPersistedString() {
		return token + PERSISTED_VALUE_SEPARATOR + secret;
	}

	public boolean isBlank() {
		return secret.length() == 0;
	}
}
