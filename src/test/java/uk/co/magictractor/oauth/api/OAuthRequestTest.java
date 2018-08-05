package uk.co.magictractor.oauth.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class OAuthRequestTest {

	@Test
	void testUrl() {
		OAuthRequest request = setUpExampleRequest();
		// TODO! hamcrest
		// assertThat(request.getUrl(), Matchers.eq)
		assertEquals("https://api.flickr.com/services/rest/request_token", request.getUrl());
	}

	// TODO! the example does not document which secret is used to generate the
	// signature?!
//	@Test
//	void testSignatureBaseString() {
//		OAuthRequest request = setUpExampleRequest();
//		assertEquals(
//				"GET&https%3A%2F%2Fwww.flickr.com%2Fservices%2Foauth%2Frequest_token&oauth_callback%3Dhttp%253A%252F%252Fwww.example.com%26oauth_consumer_key%3D653e7a6ecc1d528c516cc8f92cf98611%26oauth_nonce%3D95613465%26oauth_signature_method%3DHMAC-SHA1%26oauth_timestamp%3D1305586162%26oauth_version%3D1.0",
//				request.getSignatureBaseString());
//	}

//	@Test
//	void testSignature() {
//		OAuthRequest request = setUpExampleRequest();
//		assertEquals("7w18YS2bONDPL%2FzgyzP5XTr5af4%3D", request.getSignature());
//	}

	/** Set up example from https://www.flickr.com/services/api/auth.oauth.html */
	private OAuthRequest setUpExampleRequest() {
		OAuthRequest request = new OAuthRequest("https://api.flickr.com/services/rest/request_token");

		// request.removeParam("format");
		// request.removeParam("nojsoncallback");

		// Note that the nonce and timestamp in the example doesn't match in the example
		// request and base string
		request.setParam("oauth_nonce", "95613465");
		request.setParam("oauth_timestamp", "1305586162");
		request.setParam("oauth_consumer_key", "653e7a6ecc1d528c516cc8f92cf98611");
		request.setParam("oauth_callback", "oob");

//		https://www.flickr.com/services/oauth/request_token
//?oauth_nonce=89601180
//&oauth_timestamp=1305583298
//&oauth_consumer_key=653e7a6ecc1d528c516cc8f92cf98611
//&oauth_signature_method=HMAC-SHA1
//&oauth_version=1.0
//&oauth_callback=http%3A%2F%2Fwww.example.com

		return request;
	}
}
