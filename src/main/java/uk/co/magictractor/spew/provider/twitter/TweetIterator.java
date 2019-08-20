package uk.co.magictractor.spew.provider.twitter;

import java.util.List;

import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.OutgoingHttpRequest;
import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class TweetIterator<E> extends PageTokenServiceIterator<E> {

    // If non-null fetch tweets for user with this screen name, otherwise tweets are fetched for the authenticated user.
    private String screenName;

    private TweetIterator() {
    }

    @Override
    protected PageAndNextToken<E> fetchPage(String pageToken) {
        OutgoingHttpRequest request = getApplication()
                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

        getLogger().debug("set max_id={}", pageToken);
        request.setQueryStringParam("max_id", pageToken);

        request.setQueryStringParam("screen_name", screenName);

        request.setQueryStringParam("count", getPageSize());

        // Get full tweet text.
        // See https://developer.twitter.com/en/docs/tweets/tweet-updates
        request.setQueryStringParam("tweet_mode", "extended");

        // Exclude retweets
        request.setQueryStringParam("include_rts", "false");
        // Exclude replies
        request.setQueryStringParam("exclude_replies", "true");

        // Omit user info
        request.setQueryStringParam("trim_user", "true");

        SpewParsedResponse response = request.sendRequest();

        List<E> page = response.getList("$", getElementType());

        String nextToken;
        if (!page.isEmpty()) {
            long lastTweetId = response.getLong("$.[-1].id");
            nextToken = Long.toString(lastTweetId - 1);
        }
        else {
            nextToken = null;
        }

        return new PageAndNextToken<>(page, nextToken);
    }

    public static class TweetIteratorBuilder<E>
            extends PageTokenServiceIteratorBuilder<E, TweetIterator<E>, TweetIteratorBuilder<E>> {

        public TweetIteratorBuilder(SpewApplication application, Class<E> elementType) {
            super(application, elementType, new TweetIterator<>());
        }

        public TweetIteratorBuilder<E> withScreenName(String screenName) {
            getIteratorInstance().screenName = screenName;
            return this;
        }
    }

}
