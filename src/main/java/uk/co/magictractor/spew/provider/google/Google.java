package uk.co.magictractor.spew.provider.google;

import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.LocalDateTimeTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

// https://aaronparecki.com/oauth-2-simplified/#web-server-apps
//
//https://developers.google.com/identity/protocols/OAuth2InstalledApp
// https://developers.google.com/identity/protocols/OAuth2WebServer
//
// Uses OAuth2
//Client ID
//346766315499-60ikghor22r0lkbdtqp6jpgvtpff8vg3.apps.googleusercontent.com
//Client Secret
//-JW9p0euMrM-ymQgeqEJ1MvZ
public class Google implements SpewOAuth2ServiceProvider {

    private Google() {
    }

    @Override
    public String oauth2AuthorizationUri() {
        return "https://accounts.google.com/o/oauth2/v2/auth";
    }

    @Override
    public String oauth2TokenUri() {
        return "https://www.googleapis.com/oauth2/v4/token";
    }

    // public static final String REST_ENDPOINT = "https://api.imgur.com/3/";

    //	@Override
    //	public String getTemporaryCredentialRequestUri() {
    //		return "https://www.flickr.com/services/oauth/request_token";
    //	}
    //
    //	@Override
    //	public String getResourceOwnerAuthorizationUri() {
    //		// temporaryAuthToken added
    //		return "https://www.flickr.com/services/oauth/authorize?perms=write";
    //	}
    //
    //	@Override
    //	public String getTokenRequestUri() {
    //		return "https://www.flickr.com/services/oauth/access_token";
    //	}

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
            // TODO! Z shouldn't be quoted here?? - handle offset properly
            new LocalDateTimeTypeAdapter("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }

}
