package uk.co.magictractor.oauth.api;

import java.awt.Desktop;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Date;
import java.util.Scanner;

import uk.co.magictractor.oauth.token.UserPreferencesPersister;
import uk.co.magictractor.oauth.util.ExceptionUtil;
import uk.co.magictractor.oauth.util.UrlEncoderUtil;

// https://developers.google.com/identity/protocols/OAuth2
public class OAuth2Connection extends AbstractConnection {

	/*
	 * milliseconds to remove from expiry to ensure that we refresh if getting close
	 * to the server's expiry time
	 */
	private static final int EXPIRY_BUFFER = 60;

	private final OAuth2Server authServer;

	private final UserPreferencesPersister accessToken;
	// milliseconds since start of epoch
	private final UserPreferencesPersister accessTokenExpiry;
	private final UserPreferencesPersister refreshToken;
	// TODO! and expiry info

	public OAuth2Connection(OAuth2Server authServer) {
		this.authServer = authServer;

		this.accessToken = new UserPreferencesPersister(authServer, "access_token");
		this.accessTokenExpiry = new UserPreferencesPersister(authServer, "access_token_expiry");
		this.refreshToken = new UserPreferencesPersister(authServer, "refresh_token");
	}

	public OAuthResponse request(OAuthRequest apiRequest) {
		// authenticate();

		if (accessToken.getValue() == null) {
			authenticateUser();
		} else if (isAccessTokenExpired()) {
			fetchRefreshedAccessToken();
		}

		// TODO! (optionally?) verify existing tokens?
//		if (userTokenAndSecret.isBlank()) {
//			authenticateUser();
//		}
//
//		forAll(apiRequest);
//		forApi(apiRequest);

		// apiRequest.s
		return ExceptionUtil.call(() -> request0(apiRequest, authServer.getJsonConfiguration(), this::setAuthHeader));
	}

	private void setAuthHeader(HttpURLConnection con) {
		con.setRequestProperty("Authorization", "Bearer " + accessToken.getValue());
	}

	protected String getUrl(OAuthRequest request) {
		// String unsignedUrl = request.getUrl() + "?" +
		// getQueryString(request.getParams());

		// urlBuilder.append("oauth_signature=");
		// urlBuilder.append(getSignature());

		return request.getUrl() + "?" + getQueryString(request.getParams(), UrlEncoderUtil::paramEncode);
	}

	private void authenticateUser() {
		authorize();

		Scanner scanner = new Scanner(System.in);
		// System.err.println("Enter verification code for oauth_token=" + requestToken
		// + ": ");
		System.err.println("Enter verification code: ");
		String verification = scanner.nextLine().trim();
		// FlickrConfig.setUserAuthVerifier(verification);
		scanner.close();

		// verify(verification);

		fetchAccessAndRefreshToken(verification);
	}

	private void authorize() {
		OAuthRequest request = new OAuthRequest(authServer.getAuthorizationUri());

		// TODO! props file
		// Client ID
		// 346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com
		// Client Secret
		// -JW9p0euMrM-ymQgeqEJ1MvZ
		request.setParam("client_id", "346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com");
		// TODO! change to urn:ietf:wg:oauth:2.0:oob:auto? - but how does code get
		// passed from browser
		request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		// request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob:auto");
		request.setParam("response_type", "code");
		// TODO! and sharing??
		// https://developers.google.com/photos/library/guides/authentication-authorization
		// TODO! move scope to auth server - or request??
		request.setParam("scope",
				"https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing");

//		OAuthResponse response = authRequest(request);
//
//		// oauth_callback_confirmed=true&oauth_token=72157697914997341-aa5c16e42e726714&oauth_token_secret=b9f69c0cb17972f6
//		String authToken = response.getString("oauth_token");
//		String authSecret = response.getString("oauth_token_secret");
		// FlickrConfig.setUserRequestToken(authToken, authSecret);
		// userTokenAndSecret = new TokenAndSecret(authToken, authSecret);

		// https://www.flickr.com/services/oauth/authorize?oauth_token=72157697915783691-9402de420a27bdea&perms=write
		// String authUrl =
		// "https://www.flickr.com/services/oauth/authorize?oauth_token=" + authToken +
		// "&perms=write";

		// TODO! check whether this already contains question mark
		// String authUrl = authServer.getResourceOwnerAuthorizationUri() + "&" +
		// "oauth_token=" + authToken;

		// https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
		if (Desktop.isDesktopSupported()) {
			// uri = new
			String authUrl = getUrl(request);
			ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(authUrl)));
		} else {
			throw new UnsupportedOperationException("TODO");
		}
	}

	private void fetchAccessAndRefreshToken(String code) {
		OAuthRequest request = new OAuthRequest(authServer.getTokenUri());

		// ah! needed to be POST else 404
		request.setHttpMethod("POST");

		request.setParam("code", code);
		// TODO! move these to props...
		request.setParam("client_id", "346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com");
		request.setParam("client_secret", "-JW9p0euMrM-ymQgeqEJ1MvZ");

		request.setParam("grant_type", "authorization_code");
		// request.setParam("redirect_uri",
		// "https://www.googleapis.com/auth/photoslibrary");
		// request.setParam("redirect_uri", "https://magictractor.co.uk");

		// request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob:auto");
		request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		// request.setParam("scope", "");

		OAuthResponse response = authRequest(request);

		refreshToken.setValue(response.getString("refresh_token"));
		// accessToken.setValue(response.getString("access_token"));
		// System.err.println("access_token set to " + accessToken.getValue());
		setAccessToken(response);
	}

	// TODO! handle invalid/expired refresh tokens
	// https://developers.google.com/identity/protocols/OAuth2InstalledApp#offline
	private void fetchRefreshedAccessToken() {
		OAuthRequest request = new OAuthRequest(authServer.getTokenUri());

		// ah! needed to be POST else 404
		request.setHttpMethod("POST");

		request.setParam("refresh_token", refreshToken.getValue());
		// TODO! move these to props...
		request.setParam("client_id", "346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com");
		request.setParam("client_secret", "-JW9p0euMrM-ymQgeqEJ1MvZ");

		request.setParam("grant_type", "refresh_token");
		OAuthResponse response = authRequest(request);

		// accessToken.setValue(response.getString("access_token"));
		// System.err.println("accessToken refreshed to " + accessToken.getValue());

		setAccessToken(response);
	}

	private void setAccessToken(OAuthResponse response) {
		accessToken.setValue(response.getString("access_token"));

		// typically 3600 for one hour
		int expiresInSeconds = Integer.parseInt(response.getString("expires_in"));
		long expiresInMilliseconds = expiresInSeconds * 1000;
		long expiry = System.currentTimeMillis() + expiresInMilliseconds - EXPIRY_BUFFER;
		accessTokenExpiry.setValue(Long.toString(expiry));

		// temp!
		long diff = expiry - new Date().getTime();
		System.err.println("diff: " + diff); // -7696?!?

		System.err.println("accessToken set: " + accessToken.getValue() + " expires " + accessTokenExpiry.getValue());
	}

	private boolean isAccessTokenExpired() {
		long expiry = Long.parseLong(accessTokenExpiry.getValue());
		boolean isExpired = System.currentTimeMillis() - expiry > 0;

		if (isExpired) {
			System.err.println("accessToken has expired");
		}

		return isExpired;
	}

//	{
//		  "access_token": "ya29.GltcBrSQ1GX2N6sN57ktc1smgmocYpP1MKgn_wPkJRpu0KcTxgDNW7r4UBg3w3rK0J6B3tQI-OjIgFuHDXBmY3a4--7Jj3qy6saDIYbrdYobv3jVxrMA4B3hEdGn",
//		  "expires_in": 3600,
//		  "refresh_token": "1/gLkG1sNlUr3U3T-TVWtX37jOe40f6eQvgoFLG_26mfs",
//		  "scope": "https://www.googleapis.com/auth/photoslibrary https://www.googleapis.com/auth/photoslibrary.sharing",
//		  "token_type": "Bearer"
//		}

	private OAuthResponse authRequest(OAuthRequest apiRequest) {
		// forAll(apiRequest);
		return ExceptionUtil.call(() -> request0(apiRequest, authServer.getJsonConfiguration()));
	}

}
