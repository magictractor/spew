package uk.co.magictractor.spew.api;

import java.util.function.BiFunction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import uk.co.magictractor.spew.access.AuthorizationHandler;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.server.LocalServerAuthorizationHandler;
import uk.co.magictractor.spew.util.ContentTypeUtil;

// Base interface for OAuth1 and OAuth2. Some methods likely to move here from OAuth1 when (and if) OAuth2 support is added.
// https://stackoverflow.com/questions/4113934/how-is-oauth-2-different-from-oauth-1
// imgur and google photos uses OAuth2 https://api.imgur.com/#oauthendpoints
// imgur responses also give information about how many calls by follow in the same hour
// google photos uses "next page" tokens
// interesting! https://hueniverse.com/oauth-2-0-and-the-road-to-hell-8eec45921529
//
// https://en.wikipedia.org/wiki/List_of_OAuth_providers
public interface SpewServiceProvider {

    /**
     * <p>
     * May be overridden to ensure that every request has standard parameters.
     * This can be used, for example, to set a parameter which specifies that
     * responses should be in JSON format.
     * </p>
     * <p>
     * Default implementation does nothing.
     * </p>
     */
    default void prepareRequest(SpewRequest request) {
        // Do nothing
    }

    /**
     * <p>
     * May be overridden for service provides which misreport content types in
     * the HTTP header.
     * </p>
     */
    default String getContentType(SpewResponse response) {
        return ContentTypeUtil.fromHeader(response);
    }

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

    default AuthorizationHandler getDefaultAuthorizationHandler(
            BiFunction<String, String, Boolean> verificationFunction) {
        return new LocalServerAuthorizationHandler(verificationFunction);
    }

    default String appManagementUrl() {
        throw new UnsupportedOperationException("Not recorded for this site");
    }

}
