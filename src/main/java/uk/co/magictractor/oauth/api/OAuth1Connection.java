package uk.co.magictractor.oauth.api;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import uk.co.magictractor.oauth.token.PropertyFileTokenAndSecretPersister;
import uk.co.magictractor.oauth.token.TokenAndSecret;
import uk.co.magictractor.oauth.token.TokenAndSecretPersister;
import uk.co.magictractor.oauth.token.UserPreferencesTokenAndSecretPersister;
import uk.co.magictractor.oauth.util.ExceptionUtil;
import uk.co.magictractor.oauth.util.UrlEncoderUtil;

// TODO! common interface for OAuth1 and OAuth2 connections (and no auth? / other auth?)
public final class OAuth1Connection extends AbstractOAuthConnection {

	// TODO! the this the java name, want to derive it from
	// OAuthServer.getSignature()
	private static final String SIGNATURE_METHOD = "HmacSHA1";

	private final OAuth1ServiceProvider authServer;
	private final TokenAndSecret appTokenAndSecret;
	/**
	 * This is only used for the access token. Temporary tokens are held in memory
	 * and not persisted.
	 */
	private final TokenAndSecretPersister userTokenAndSecretPersister;
	private TokenAndSecret userTokenAndSecret;

	// TODO! could derive the authServer from the service URL?
	// TODO! change this to default then use MyApp.getInstance().getConnection()
	/** Default visibility, applications should obtain instances via OAuth1Application.getConnection(). */
	public OAuth1Connection(OAuth1Application application) {
		this.authServer = application.getServiceProvider();
		// TODO! split app from service provider, as done for Google Photos (OAuth2)
		this.appTokenAndSecret = new PropertyFileTokenAndSecretPersister(authServer).getTokenAndSecret();
		this.userTokenAndSecretPersister = new UserPreferencesTokenAndSecretPersister(authServer);
		this.userTokenAndSecret = new UserPreferencesTokenAndSecretPersister(authServer).getTokenAndSecret();
	}

	public OAuthResponse request(OAuthRequest apiRequest) {
		// TODO! (optionally?) verify existing tokens?
		if (userTokenAndSecret.isBlank()) {
			authenticateUser();
		}

		forAll(apiRequest);
		forApi(apiRequest);
		return ExceptionUtil.call(() -> request0(apiRequest, authServer.getJsonConfiguration()));
	}

	private OAuthResponse authRequest(OAuthRequest apiRequest) {
		forAll(apiRequest);
		return ExceptionUtil.call(() -> request0(apiRequest, authServer.getJsonConfiguration()));
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

		verify(verification);
	}

	private void authorize() {
		// TODO! POST?
		OAuthRequest request = OAuthRequest.createGetRequest(authServer.getTemporaryCredentialRequestUri());
		OAuthResponse response = authRequest(request);

		// oauth_callback_confirmed=true&oauth_token=72157697914997341-aa5c16e42e726714&oauth_token_secret=b9f69c0cb17972f6
		String authToken = response.getString("oauth_token");
		String authSecret = response.getString("oauth_token_secret");
		// FlickrConfig.setUserRequestToken(authToken, authSecret);
		userTokenAndSecret = new TokenAndSecret(authToken, authSecret);

		// https://www.flickr.com/services/oauth/authorize?oauth_token=72157697915783691-9402de420a27bdea&perms=write
		// String authUrl =
		// "https://www.flickr.com/services/oauth/authorize?oauth_token=" + authToken +
		// "&perms=write";

		// TODO! check whether this already contains question mark
		String authUrl = authServer.getResourceOwnerAuthorizationUri() + "&" + "oauth_token=" + authToken;

		// https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
		if (Desktop.isDesktopSupported()) {
			// uri = new
			ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(authUrl)));
		} else {
			throw new UnsupportedOperationException("TODO");
		}
	}

	private void verify(String verification) {
		// FlickrRequest request = FlickrRequest.forAuth("access_token");
		// TODO! POST?
		OAuthRequest request = OAuthRequest.createGetRequest(authServer.getTokenRequestUri());
		request.setParam("oauth_token", userTokenAndSecret.getToken());
		request.setParam("oauth_verifier", verification);
		OAuthResponse response = authRequest(request);

		String authToken = response.getString("oauth_token");
		String authSecret = response.getString("oauth_token_secret");
		userTokenAndSecret = new TokenAndSecret(authToken, authSecret);
		userTokenAndSecretPersister.setTokenAndSecret(userTokenAndSecret);
	}

	// hmms - push some of this up? - encode could be different per connection?
	protected String getUrl(OAuthRequest request) {
		// String unsignedUrl = request.getUrl() + "?" +
		// getQueryString(request.getParams());

		// urlBuilder.append("oauth_signature=");
		// urlBuilder.append(getSignature());

		return request.getUrl() + "?" + getQueryString(request.getParams(), UrlEncoderUtil::paramEncode)
				+ "&oauth_signature=" + getSignature(request);
	}

	// See https://www.flickr.com/services/api/auth.oauth.html
	// https://gist.github.com/ishikawa/88599/3195bdeecabeb38aa62872ab61877aefa6aef89e
	private String getSignature(OAuthRequest request) {
//		if (userSecret == null) {
//			throw new IllegalArgumentException("userSecret must not be null (it may be an empty string)");
//		}

		// TODO! consumer key only absent for auth
		// String key = FlickrConfig.API_KEY + "&";
		String key = appTokenAndSecret.getSecret() + "&" + userTokenAndSecret.getSecret();
		// TODO! Java signature name and Api not identical
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), SIGNATURE_METHOD);
		Mac mac = ExceptionUtil.call(() -> Mac.getInstance(SIGNATURE_METHOD));
		ExceptionUtil.call(() -> mac.init(signingKey));

		String signature = Base64.getEncoder().encodeToString(mac.doFinal(getSignatureBaseString(request).getBytes()));
		return ExceptionUtil.call(() -> URLEncoder.encode(signature, "UTF-8"));
	}

	// See https://www.flickr.com/services/api/auth.oauth.html
	public String getSignatureBaseString(OAuthRequest request) {
//		String baseStringUrl = getQueryString(getBaseStringParams(request));
//
//		String encodedUrl = ExceptionUtil.call(() -> URLEncoder.encode(baseStringUrl, "UTF-8"));
		// TODO! very scruffy
		// replace the question mark with an ampersand
		// encodedUrl = encodedUrl.replace("%3F", "&");

		// return request.getHttpMethod() + "&" + encodedUrl + "&" + encodedQueryParams;

		StringBuilder signatureBaseStringBuilder = new StringBuilder();
		signatureBaseStringBuilder.append(request.getHttpMethod());
		signatureBaseStringBuilder.append('&');
		signatureBaseStringBuilder.append(UrlEncoderUtil.oauthEncode(request.getUrl()));
		signatureBaseStringBuilder.append('&');
		signatureBaseStringBuilder.append(
				UrlEncoderUtil.oauthEncode(getQueryString(getBaseStringParams(request), UrlEncoderUtil::oauthEncode)));

		return signatureBaseStringBuilder.toString();
	}

//	private String urlEncode(String string) {
//		return ExceptionUtil.call(() -> URLEncoder.encode(string, "UTF-8"));
//	}

	/** @return ordered params for building signature key */
	private Map<String, Object> getBaseStringParams(OAuthRequest request) {
		// TODO! some params should be ignored
		// TODO! where should params be escaped??
		return new TreeMap<>(request.getParams());
	}

	private void forApi(OAuthRequest request) {
		// OAuthRequest request = new OAuthRequest(FLICKR_REST_ENDPOINT);
//		setParam("oauth_consumer_key", FlickrConfig.API_KEY);

		request.setParam("api_key", appTokenAndSecret.getToken());
		request.setParam("oauth_token", userTokenAndSecretPersister.getTokenAndSecret().getToken());
		// request.setParam("method", flickrMethod);
		request.setParam("format", "json");
		request.setParam("nojsoncallback", "1");

		// return request;
	}

	private void forAll(OAuthRequest request) {
		// hmm... same as api_key?
		request.setParam("oauth_consumer_key", appTokenAndSecret.getToken());

		// TODO! nonce should be random, with guarantee that it is never the same if the
		// timestamp has not
		// move on since the last API call
		// TODO! how to make this testable (should be non-random during testing, but
		// Spring too heavyweight)
		// https://oauth.net/core/1.0a/#nonce
		request.setParam("oauth_nonce", new Random().nextInt());
		request.setParam("oauth_timestamp", System.currentTimeMillis());
		// setParam("oauth_callback", "www.google.com");
		// "oob" so that web shows the verifier which can then be copied
		request.setParam("oauth_callback", "oob");
		// addParam("oauth_signature_method", SIGNATURE_METHOD);
		request.setParam("oauth_version", "1.0");
		// TODO (tbc) method name not same in Java and API
		request.setParam("oauth_signature_method", "HMAC-SHA1");
	}

}

//  https://api.flickr.com/services/rest/?method=flickr.photos.setMeta&api_key=5939e168bc6ea2e41e83b74f6f0b3e2d&photo_id=45249983521&title=Small+white&format=json&nojsoncallback=1&auth_token=72157672277056577-9fa9087d61430e0a&api_sig=2e45756e2c8c451dc08468ab15b7c9ac