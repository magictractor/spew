package uk.co.magictractor.spew.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

// Base interface for OAuth1 and OAuth2. Some methods likely to move here from OAuth1 when (and if) OAuth2 support is added.
// https://stackoverflow.com/questions/4113934/how-is-oauth-2-different-from-oauth-1
// imgur and google photos uses OAuth2 https://api.imgur.com/#oauthendpoints
// imgur responses also give information about how many calls by follow in the same hour
// google photos uses "next page" tokens
// interesting! https://hueniverse.com/oauth-2-0-and-the-road-to-hell-8eec45921529
//
// https://en.wikipedia.org/wiki/List_of_OAuth_providers
public interface SpewServiceProvider {

    // TODO! move Gson away from here
    default GsonBuilder getGsonBuilder() {
        return new GsonBuilder();
    }

    // TODO! generalise type mappings so they can be used for both Json and XML
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

    /**
     * Check that the response has an "ok" status (if it contains a status) and
     * no error message (if it can contain an error message). TODO! what should
     * this throw?
     *
     * @throws BadResponseException if the response contains an error message or
     *         has a status other than "ok".
     */
    // TODO! force all service providers to implement this
    // void verifyResponse(SpewResponse response);

    // TODO! after response parsers are added ensure verifyResponse is still called
    default void verifyResponse(SpewParsedResponse response) {
    }

}
