package uk.co.magictractor.spew.twitter;

import uk.co.magictractor.spew.api.OAuth1ServiceProvider;

// manage apps at apps.twitter.com
// Twitter can be accessed via OAuth1 or OAuth2 - names TwitterOAuth1 and TwitterOAuth2?
public class Twitter implements OAuth1ServiceProvider {

    private static final Twitter INSTANCE = new Twitter();

    public static Twitter getInstance() {
        return INSTANCE;
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/request_token
    @Override
    public String getTemporaryCredentialRequestUri() {
        // TODO! should be POST
        return "https://api.twitter.com/oauth/request_token";
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/authenticate
    @Override
    public String getResourceOwnerAuthorizationUri() {
        return "https://api.twitter.com/oauth/authenticate";
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/access_token
    @Override
    public String getTokenRequestUri() {
        return "https://api.twitter.com/oauth/access_token";
    }

    @Override
    public String getRequestSignatureMethod() {
        // TODO! check this
        return "HMAC-SHA1";
    }

}
