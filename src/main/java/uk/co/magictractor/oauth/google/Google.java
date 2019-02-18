package uk.co.magictractor.oauth.google;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;

import uk.co.magictractor.oauth.api.OAuth2Server;
import uk.co.magictractor.oauth.flickr.json.TagSetTypeAdapter;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.json.BooleanTypeAdapter;
import uk.co.magictractor.oauth.json.InstantTypeAdapter;
import uk.co.magictractor.oauth.json.LocalDateTimeTypeAdapter;

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
public class Google implements OAuth2Server {

	@Override
	public String getAuthorizationUri() {
		return "https://accounts.google.com/o/oauth2/v2/auth";
	}

	@Override
	public String getTokenUri() {
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

	public GsonBuilder getGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();
		
		// TODO! this option and perhaps other settings should be defaults for all service providers
		//gsonBuilder.set

		gsonBuilder.setFieldNamingStrategy(new FieldNamingStrategy() {
			@Override
			public String translateName(Field f) {
				// underscore seen in machine_tags
				return f.getName().toLowerCase().replace("_", "");
			}
		});
		gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
		gsonBuilder.registerTypeAdapter(Instant.class, new InstantTypeAdapter());
		gsonBuilder.registerTypeAdapter(TagSet.class, new TagSetTypeAdapter());

		// gsonBuilder.registerTypeAdapter(List.class, new ListTypeAdapter());

		// gsonBuilder.registerTypeAdapterFactory(new FlickrTagsTypeAdapterFactory());

		return gsonBuilder;
	}
}
