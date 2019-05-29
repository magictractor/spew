package uk.co.magictractor.oauth.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

// Base interface for OAuth1 and OAuth2. Some methods likely to move here from OAuth1 when (and if) OAuth2 support is added.
// https://stackoverflow.com/questions/4113934/how-is-oauth-2-different-from-oauth-1
// imgur and google photos uses OAuth2 https://api.imgur.com/#oauthendpoints
// imgur responses also give information about how many calls by follow in the same hour
// google photos uses "next page" tokens
// interesting! https://hueniverse.com/oauth-2-0-and-the-road-to-hell-8eec45921529
// 
// https://en.wikipedia.org/wiki/List_of_OAuth_providers
public interface OAuthServiceProvider {

    default GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }

    default Configuration getJsonConfiguration() {
        Gson gson = getGsonBuilder().setPrettyPrinting().create();
        JsonProvider jsonProvider = new GsonJsonProvider(gson);
        MappingProvider mappingProvider = new GsonMappingProvider(gson);

        // Option.DEFAULT_PATH_LEAF_TO_NULL required for nextPageToken used with Google paged services
        return new Configuration.ConfigurationBuilder().jsonProvider(jsonProvider)
                .mappingProvider(mappingProvider)
                .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
                .build();
    }

}
