package uk.co.magictractor.flickr.tools;

import java.awt.Desktop;
import java.net.URI;
import java.util.Scanner;

import uk.co.magictractor.flickr.api.FlickrConfig;
import uk.co.magictractor.flickr.api.FlickrConnection;
import uk.co.magictractor.flickr.api.FlickrRequest;
import uk.co.magictractor.flickr.api.FlickrResponse;
import uk.co.magictractor.flickr.util.ExceptionUtil;

public class Authenticate {

	// https://api.flickr.com/services/rest/?method=flickr.people.getPhotos
	// &api_key=1da91a5cdef62942f510c37e84ba5f70
	// &user_id=me&format=json&nojsoncallback=1
	// &auth_token=72157696367169942-cc992bdf174d50c7
	// &api_sig=2f89eec3ea014525e7a4cec5d51cf1ab

//	https://www.flickr.com/services/oauth/request_token
//		?oauth_nonce=95613465
//		&oauth_timestamp=1305586162
//		&oauth_consumer_key=653e7a6ecc1d528c516cc8f92cf98611
//		&oauth_signature_method=HMAC-SHA1
//		&oauth_version=1.0
//		&oauth_signature=7w18YS2bONDPL%2FzgyzP5XTr5af4%3D
//		&oauth_callback=http%3A%2F%2Fwww.example.com

	private void authorize() {
		FlickrRequest request = FlickrRequest.forAuth("request_token");
		FlickrResponse response = FlickrConnection.request(request);

		// oauth_callback_confirmed=true&oauth_token=72157697914997341-aa5c16e42e726714&oauth_token_secret=b9f69c0cb17972f6
		String authToken = response.getString("oauth_token");
		String authSecret = response.getString("oauth_token_secret");
		FlickrConfig.setUserRequestToken(authToken, authSecret);

		// https://www.flickr.com/services/oauth/authorize?oauth_token=72157697915783691-9402de420a27bdea&perms=write
		String authUrl = "https://www.flickr.com/services/oauth/authorize?oauth_token=" + authToken + "&perms=write";

		// https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
		if (Desktop.isDesktopSupported()) {
			// uri = new
			ExceptionUtil.call(() -> Desktop.getDesktop().browse(new URI(authUrl)));
		} else {
			throw new UnsupportedOperationException("TODO");
		}
	}

	private void verify(String verification) {
		FlickrRequest request = FlickrRequest.forAuth("access_token");
		request.setParam("oauth_token", FlickrConfig.getUserRequestToken());
		request.setParam("oauth_verifier", verification);
		FlickrResponse response = FlickrConnection.request(request);

		String authToken = response.getString("oauth_token");
		String authSecret = response.getString("oauth_token_secret");
		FlickrConfig.setUserAccessToken(authToken, authSecret);
	}

	public static void main(String[] args) {
		if (FlickrConfig.getUserAccessToken() != null) {
			System.err.println("Already have access token");
			return;
		}

		Authenticate auth = new Authenticate();

		String requestToken = FlickrConfig.getUserRequestToken();
		if (requestToken != null) {
			Scanner scanner = new Scanner(System.in);
			System.err.println("Enter verification code for oauth_token=" + requestToken + ": ");
			String verification = scanner.nextLine().trim();
			// FlickrConfig.setUserAuthVerifier(verification);
			scanner.close();
			// https://www.flickr.com/services/api/auth.oauth.html

			auth.verify(verification);
		} else {
			auth.authorize();
		}
	}
}

// jsonFlickrApi({"stat":"fail","code":100,"message":"Invalid API Key (Key has
// invalid format)"})

//https://github.com/google/google-oauth-java-client

//GET https://www.flickr.com/services/oauth/request_token ->
//oauth_problem=parameter_absent
//  &oauth_parameters_absent=
//    oauth_consumer_key
//    %26oauth_signature
//    %26oauth_signature_method
//    %26oauth_nonce
//    %26oauth_timestamp
//    %26oauth_callback

// Ah! https://www.flickr.com/services/apps/create/
// key c9b2e55ccec90ce5f5c97b9708a6031b
// secret 350878a13a2b0a8f

// oauth_callback_confirmed=true&oauth_token=72157697914997341-aa5c16e42e726714&oauth_token_secret=b9f69c0cb17972f6

// https://www.flickr.com/services/oauth/authorize?oauth_token=72157697915783691-9402de420a27bdea&perms=write
