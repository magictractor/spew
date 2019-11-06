package uk.co.magictractor.spew.provider.twitter;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.List;

import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.api.SpewOAuth2ServiceProvider;
import uk.co.magictractor.spew.core.typeadapter.InstantTypeAdapter;
import uk.co.magictractor.spew.core.typeadapter.SpewTypeAdapter;

// manage apps at apps.twitter.com
// Twitter can be accessed via OAuth1 or OAuth2 - names TwitterOAuth1 and TwitterOAuth2?
// Twitter has strict rate limiting https://developer.twitter.com/en/docs/basics/rate-limiting
public class Twitter implements SpewOAuth1ServiceProvider, SpewOAuth2ServiceProvider {

    // "created_at": "Tue May 28 12:43:55 +0000 2019"
    private static final DateTimeFormatter TWITTER_INSTANT_FORMATTER = new DateTimeFormatterBuilder()
            .appendText(ChronoField.DAY_OF_WEEK, TextStyle.SHORT)
            .appendLiteral(' ')
            .appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT)
            .appendLiteral(' ')
            .appendValue(ChronoField.DAY_OF_MONTH, 2)
            .appendLiteral(' ')
            .appendValue(ChronoField.HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .appendLiteral(' ')
            .appendOffset("+HHMM", "")
            .appendLiteral(' ')
            .appendValue(ChronoField.YEAR, 4)
            .toFormatter();

    /**
     * Twitter services typically have a default page size of 20 and a maximum
     * page size of 200. Twitter also has a restrictive rate limit (15 API calls
     * per 15 minutes), so Twitter iterator builder implementations may want to
     * set this page size by default.
     */
    // hmm max 100 at https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/get-statuses-retweets_of_me
    public static final int MAX_PAGE_SIZE = 200;

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/request_token
    @Override
    public String oauth1TemporaryCredentialRequestUri() {
        // TODO! should be POST
        return "https://api.twitter.com/oauth/request_token";
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/authenticate
    @Override
    public String oauth1ResourceOwnerAuthorizationUri() {
        return "https://api.twitter.com/oauth/authenticate";
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/access_token
    @Override
    public String oauth1TokenRequestUri() {
        return "https://api.twitter.com/oauth/access_token";
    }

    // https://developer.twitter.com/en/docs/basics/authentication/api-reference/token
    @Override
    public String oauth2TokenUri() {
        return "https://api.twitter.com/oauth2/token";
    }

    @Override
    public String oauth2AuthorizationUri() {
        throw new UnsupportedOperationException(
            "Twitter OAuth2 only supports grant_type=client_credentials, so this method should never be called");
    }

    @Override
    public String appManagementUrl() {
        return "https://developer.twitter.com/en/apps";
    }

    @Override
    public List<SpewTypeAdapter<?>> getTypeAdapters() {
        return Arrays.asList(
            new InstantTypeAdapter(TWITTER_INSTANT_FORMATTER));
    }

}
