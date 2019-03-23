package uk.co.magictractor.oauth.imgur;

import java.time.Instant;
import java.time.LocalDateTime;

import com.google.gson.GsonBuilder;

import uk.co.magictractor.oauth.api.OAuth2ServiceProvider;
import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.flickr.json.TagSetTypeAdapter;
import uk.co.magictractor.oauth.json.BooleanTypeAdapter;
import uk.co.magictractor.oauth.json.InstantTypeAdapter;
import uk.co.magictractor.oauth.json.LocalDateTimeTypeAdapter;

/**
 * From https://imgur.com/tos:
 * "don't use Imgur to host image libraries you link to from elsewhere, content
 * for your website, advertising, avatars, or anything else that turns us into
 * your content delivery network"
 */
public class Imgur implements OAuth2ServiceProvider {

	public static final String REST_ENDPOINT = "https://api.imgur.com/3/";

	private static final Imgur INSTANCE = new Imgur();

	private Imgur() {
	}

	public static Imgur getInstance() {
		return INSTANCE;
	}

	@Override
	public String getAuthorizationUri() {
		return "https://api.imgur.com/oauth2/authorize";
	}

	@Override
	public String getTokenUri() {
		return "https://api.imgur.com/oauth2/token";
	}

	public GsonBuilder getGsonBuilder() {
		GsonBuilder gsonBuilder = new GsonBuilder();

		// TODO! this option and perhaps other settings should be defaults for all
		// service providers
		// gsonBuilder.set

		gsonBuilder.registerTypeAdapter(boolean.class, new BooleanTypeAdapter());
		// TODO! Z shouldn't be quoted here?? - handle offset properly
		gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter("yyyy-MM-dd'T'HH:mm:ss'Z'"));
		gsonBuilder.registerTypeAdapter(Instant.class, InstantTypeAdapter.EPOCH_SECONDS);
		gsonBuilder.registerTypeAdapter(TagSet.class, new TagSetTypeAdapter());

		// gsonBuilder.registerTypeAdapter(List.class, new ListTypeAdapter());

		// gsonBuilder.registerTypeAdapterFactory(new FlickrTagsTypeAdapterFactory());

		return gsonBuilder;
	}
}
