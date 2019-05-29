package uk.co.magictractor.oauth.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OAuthAuthResponseTest {

    private static final String RESPONSE = "oauth_callback_confirmed=true&oauth_token=72157697915783691-9402de420a27bdea&oauth_token_secret=a1b946e524187d3d";

    @Test
    public void test() {
        OAuthAuthResponse response = new OAuthAuthResponse(RESPONSE);
        assertEquals("72157697915783691-9402de420a27bdea", response.getObject("oauth_token"));
    }
}
