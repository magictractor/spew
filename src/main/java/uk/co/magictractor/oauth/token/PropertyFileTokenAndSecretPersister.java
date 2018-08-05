package uk.co.magictractor.oauth.token;

import java.io.InputStream;
import java.util.Properties;

import uk.co.magictractor.oauth.api.OAuthServer;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class PropertyFileTokenAndSecretPersister implements TokenAndSecretPersister {

	private static final String TOKEN_KEY = "token";
	private static final String SECRET_KEY = "secret";
	
	private final TokenAndSecret tokenAndSecret;

	public PropertyFileTokenAndSecretPersister(OAuthServer authServer) {
		String fileName = authServer.getClass().getSimpleName().toLowerCase() + ".properties";
		Properties properties = new Properties();
		InputStream stream = getClass().getResourceAsStream(fileName);
		if (stream == null) {
			throw new IllegalStateException("missing application properties file " + fileName);
		}
		ExceptionUtil.call(() -> properties.load(stream));
		
		String token = properties.getProperty(TOKEN_KEY);
		String secret = properties.getProperty(SECRET_KEY);
		tokenAndSecret = new TokenAndSecret(token, secret);
	}

	@Override
	public TokenAndSecret getTokenAndSecret() {
		return tokenAndSecret;
	}

}
