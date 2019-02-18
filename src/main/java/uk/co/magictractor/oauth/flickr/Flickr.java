package uk.co.magictractor.oauth.flickr;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;

import uk.co.magictractor.oauth.api.OAuth1Server;
import uk.co.magictractor.oauth.flickr.json.TagSetTypeAdapter;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.json.BooleanTypeAdapter;
import uk.co.magictractor.oauth.json.InstantTypeAdapter;
import uk.co.magictractor.oauth.json.LocalDateTimeTypeAdapter;

// https://www.flickr.com/services/apps/create/
public class Flickr implements OAuth1Server {

	public static final String REST_ENDPOINT = "https://api.flickr.com/services/rest/";

//	https://api.imgur.com/oauth2/addclient
//		https://api.imgur.com/oauth2/authorize
//		https://api.imgur.com/oauth2/token
			
	@Override
	public String getTemporaryCredentialRequestUri() {
		return "https://www.flickr.com/services/oauth/request_token";
	}

	@Override
	public String getResourceOwnerAuthorizationUri() {
		// temporaryAuthToken added
		return "https://www.flickr.com/services/oauth/authorize?perms=write";
	}

	@Override
	public String getTokenRequestUri() {
		return "https://www.flickr.com/services/oauth/access_token";
	}

	public GsonBuilder getGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();

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
		
		//gsonBuilder.registerTypeAdapter(List.class, new ListTypeAdapter());

		//gsonBuilder.registerTypeAdapterFactory(new FlickrTagsTypeAdapterFactory());

		return gsonBuilder;
	}
}
