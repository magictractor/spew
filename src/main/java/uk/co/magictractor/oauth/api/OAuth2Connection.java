package uk.co.magictractor.oauth.api;

import java.awt.Desktop;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.jayway.jsonpath.Configuration;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import uk.co.magictractor.oauth.server.CallbackServer;
import uk.co.magictractor.oauth.token.UserPreferencesPersister;
import uk.co.magictractor.oauth.util.ExceptionUtil;
import uk.co.magictractor.oauth.util.UrlEncoderUtil;

// https://developers.google.com/identity/protocols/OAuth2
public class OAuth2Connection extends AbstractOAuthConnection<OAuth2Application, OAuth2ServiceProvider> {

	private static final String OOB = "urn:ietf:wg:oauth:2.0:oob";
	private static final String CALLBACK_SERVER = "http://127.0.0.1:8080";

	/*
	 * milliseconds to remove from expiry to ensure that we refresh if getting close
	 * to the server's expiry time
	 */
	private static final int EXPIRY_BUFFER = 60;

	// private final OAuth2Application application;

	private final UserPreferencesPersister accessToken;
	// milliseconds since start of epoch
	private final UserPreferencesPersister accessTokenExpiry;
	private final UserPreferencesPersister refreshToken;

	/// ** Default visibility, applications should obtain instances via
	/// OAuth2Application.getConnection(). */
	// TODO! change to default?
	public OAuth2Connection(OAuth2Application application) {
		super(application);
		// this.application = application;

		this.accessToken = new UserPreferencesPersister(application, "access_token");
		this.accessTokenExpiry = new UserPreferencesPersister(application, "access_token_expiry");
		this.refreshToken = new UserPreferencesPersister(application, "refresh_token");
	}

	public OAuthResponse request(OAuthRequest apiRequest) {
		// authenticate();

		if (accessToken.getValue() == null) {
			// authenticateUser();
			authorize();
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

		// TODO! need to block while waiting for auth from Imgur
		// return null;
		return ExceptionUtil.call(() -> request0(apiRequest, getJsonConfiguration(), this::setAuthHeader));
	}

	private Configuration getJsonConfiguration() {
		return getServiceProvider().getJsonConfiguration();
	}

	private void setAuthHeader(HttpURLConnection con) {
		con.setRequestProperty("Authorization", "Bearer " + accessToken.getValue());
	}

	protected String getUrl(OAuthRequest request) {
		// String unsignedUrl = request.getUrl() + "?" +
		// getQueryString(request.getParams());

		// urlBuilder.append("oauth_signature=");
		// urlBuilder.append(getSignature());

		// TODO! may need to have both query params and body params
		if (!request.hasParamsInBody()) {
			return request.getUrl() + "?" + getQueryString(request.getParams(), UrlEncoderUtil::paramEncode);
		} else {
			// Params will go in the request body.
			return request.getUrl();
		}
		// return request.getUrl() + "?" + getQueryString(request.getParams(), (s) ->
		// s);
	}

//	private void authenticateUser() {
//		authorize();
//
//		Scanner scanner = new Scanner(System.in);
//		// System.err.println("Enter verification code for oauth_token=" + requestToken
//		// + ": ");
//		System.err.println("Enter verification code: ");
//		String verification = scanner.nextLine().trim();
//		// FlickrConfig.setUserAuthVerifier(verification);
//		scanner.close();
//
//		// verify(verification);
//
//		fetchAccessAndRefreshToken(verification);
//	}

	// https://developers.google.com/photos/library/guides/authentication-authorization
	private void authorize() {
		OAuthRequest request = OAuthRequest.createGetRequest(getServiceProvider().getAuthorizationUri());

		request.setParam("client_id", getApplication().getClientId());
		String redirectUri = getAuthorizeRedirectUrl();
		request.setParam("redirect_uri", redirectUri);

		// Ah! was using code, but should be token? see
		// https://apidocs.imgur.com/#authorization-and-oauth
		// ah! despite being deprecated, pin works for Imgur - first step only - cannot
		// convert pin to access token
		// Google does not like "pin": Invalid response_type: pin
		// request.setParam("response_type", "pin");
		request.setParam("response_type", getAuthorizeResponseType());
		// BUT! Google bombs with token plus redirect_uri
		// redirect_uri not supported for response_type=token: urn:ietf:wg:oauth:2.0:oob

		request.setParam("scope", getApplication().getScope());

		// Igmur:
		// http://127.0.0.1:8080/#access_token=76fc8472073f9ae9da616bae08fc686a6395a41d
		// &expires_in=315360000
		// &token_type=bearer
		// &refresh_token=70975f674b1218fa535167bdc01ba943123ec935
		// &account_username=GazingAtTrees
		// &account_id=96937645

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
		if (!Desktop.isDesktopSupported()) {
			throw new UnsupportedOperationException("TODO");
		}

		// Launch local server to receive callback containing authentication
		// information.
		CallbackServer callbackServer = null;
		if (CALLBACK_SERVER.equals(redirectUri)) {
			callbackServer = new CallbackServer(this::setAccessToken, 8080);
			callbackServer.run();
		}
//		else if (OOB.equals(redirectUri)) {
//			captureManuallyPastedGrant();
//		}

		String authUrl = getUrl(request);
		ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(authUrl)));

		if (callbackServer != null) {
			callbackServer.join();
		} else if (OOB.equals(redirectUri)) {
			captureManuallyPastedGrant();
		} // else boom
	}

	// TODO! some of this is common with OAuth1
	private void captureManuallyPastedGrant() {
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

	// future - perhaps allow connection to use value other than the default
	private String getAuthorizeResponseType() {
		return getApplication().defaultAuthorizeResponseType().name().toLowerCase();
	}

	// future - perhaps allow connection to use value other than the default
	private String getAuthorizeRedirectUrl() {
		switch (getApplication().defaultAuthorizeResponseType()) {
		case TOKEN:
			return CALLBACK_SERVER;
		default:
			return OOB;
		}
	}

	// TODO! needs a tweak to handle pin
	private void fetchAccessAndRefreshToken(String code) {
		// ah! needed to be POST else 404 (Google)
		OAuthRequest request = OAuthRequest.createPostRequest(getServiceProvider().getTokenUri());

		request.setParam("code", code);
		// request.setParam("pin", code);
		request.setParam("client_id", getApplication().getClientId());
		request.setParam("client_secret", getApplication().getClientSecret());

		request.setParam("grant_type", "authorization_code");
		// request.setParam("grant_type", "pin");

		// request.setParam("redirect_uri",
		// "https://www.googleapis.com/auth/photoslibrary");
		// request.setParam("redirect_uri", "https://magictractor.co.uk");

		// request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob:auto");
		// Hmm. This looks unnecessary... but Google needs it
		// request.setParam("redirect_uri", "urn:ietf:wg:oauth:2.0:oob");
		request.setParam("redirect_uri", OOB);
		// request.setParam("scope", "");

		OAuthResponse response = authRequest(request);

		refreshToken.setValue(response.getString("refresh_token"));
		// accessToken.setValue(response.getString("access_token"));
		// System.err.println("access_token set to " + accessToken.getValue());
		setAccessToken((key) -> response.getString(key));
	}

	// TODO! handle invalid/expired refresh tokens
	// https://developers.google.com/identity/protocols/OAuth2InstalledApp#offline
	private void fetchRefreshedAccessToken() {
		OAuthRequest request = OAuthRequest.createPostRequest(getServiceProvider().getTokenUri());

		request.setParam("refresh_token", refreshToken.getValue());
		request.setParam("client_id", getApplication().getClientId());
		request.setParam("client_secret", getApplication().getClientSecret());

		request.setParam("grant_type", "refresh_token");
		OAuthResponse response = authRequest(request);

		// accessToken.setValue(response.getString("access_token"));
		// System.err.println("accessToken refreshed to " + accessToken.getValue());

		setAccessToken((key) -> response.getString(key));
	}

//	private void setAccessToken(OAuthResponse response) {
//		setAccessToken(response.getString("access_token"), response.getString("expires_in"));
//	}

	private void setAccessToken(FullHttpRequest httpRequest) {
		ByteBuf content = httpRequest.content();
		// new QueryStringDecoder()

		String s = content.toString(Charsets.UTF_8);
		System.err.println("content: " + s);

		QueryStringDecoder d = new QueryStringDecoder(s, Charsets.UTF_8, false);
		Map<String, List<String>> parameters = d.parameters();
		System.err.println(parameters);

		setAccessToken((key) -> Iterables.getOnlyElement(parameters.get(key)));
	}

	private void setAccessToken(Function<String, String> valueMap) {
		accessToken.setValue(valueMap.apply("access_token"));

		// typically 3600 for one hour
		int expiresInSeconds = Integer.parseInt(valueMap.apply("expires_in"));
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
		return ExceptionUtil.call(() -> request0(apiRequest, getJsonConfiguration()));
	}

}
