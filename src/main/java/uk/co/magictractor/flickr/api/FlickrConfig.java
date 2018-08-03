package uk.co.magictractor.flickr.api;

import java.util.prefs.Preferences;

import uk.co.magictractor.flickr.util.ExceptionUtil;

// TODO! properties file? - DO NOT COMMIT with SECRET here?
public class FlickrConfig {

	// API_KEY and API_SECRET from https://www.flickr.com/services/apps/create/
	// To modify app, see https://www.flickr.com/services/apps/by/gazingattrees
	static final String API_KEY = "c9b2e55ccec90ce5f5c97b9708a6031b";
	static final String API_SECRET = "350878a13a2b0a8f";

	static String userTokenType;
	static String userToken;
	static String userSecret = "";

	private FlickrConfig() {
	}

	// Bah! https://bugs.openjdk.java.net/browse/JDK-8139507
	// https://coderanch.com/t/635066/java/create-prefs-warning-Windows
	private static Preferences userConfig() {
		return Preferences.userNodeForPackage(FlickrConfig.class);
	}

	public static String getUserRequestToken() {
		ensureUserToken();
		if ("request".equals(userTokenType)) {
			return userToken;
		}
		return null;
	}

	public static void setUserRequestToken(String token, String secret) {
		setUserToken("request", token, secret);
	}

	public static String getUserAccessToken() {
		ensureUserToken();
		if ("access".equals(userTokenType)) {
			return userToken;
		}
		return null;
	}

	public static String getUserSecret() {
		// ensureUserToken();
		return userSecret;
	}

	public static void setUserAccessToken(String token, String secret) {
		setUserToken("access", token, secret);
	}

	private static void setUserToken(String tokenType, String token, String secret) {
		Preferences userConfig = userConfig();
		// ExceptionUtil.call(() -> userConfig.clear());
		userConfig.put("token", tokenType + ":" + token + ":" + secret);
	}

	private static void ensureUserToken() {
		if (userTokenType == null) {
			String tokenInfo = userConfig().get("token", null);
			if (tokenInfo != null) {
				String[] tokenInfoParts = tokenInfo.split(":");
				userTokenType = tokenInfoParts[0];
				userToken = tokenInfoParts[1];
				userSecret = tokenInfoParts[2];
			}
		}
	}

	public static void resetUser() {
		ExceptionUtil.call(() -> userConfig().clear());
	}

//	public static String getUserAuthVerifier() {
//		return userConfig().get("authVerifier", null);
//	}
//
//	public static void setUserAuthVerifier(String authToken) {
//		// TODO! don't need to persist this - can exchange for access token immediately
//		userConfig().put("authVerifier", authToken);
//	}

	// token type: auth or access

	// authorize
}

//Ah! https://www.flickr.com/services/apps/create/
//key c9b2e55ccec90ce5f5c97b9708a6031b
//secret 350878a13a2b0a8f
