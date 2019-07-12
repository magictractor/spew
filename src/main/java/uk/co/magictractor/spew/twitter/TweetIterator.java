package uk.co.magictractor.spew.twitter;

import java.util.List;

import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.SpewRequest;
import uk.co.magictractor.spew.api.SpewResponse;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class TweetIterator<E> extends PageTokenServiceIterator<E> {

    // If non-null fetch tweets for user with this screen name, otherwise tweets are fetched for the authenticated user.
    private String screenName;

    private TweetIterator() {
    }

    @Override
    protected PageAndNextToken<E> fetchPage(String pageToken) {
        SpewRequest request = SpewRequest
                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

        System.err.println("set max_id=" + pageToken);
        request.setParam("max_id", pageToken);

        request.setParam("screen_name", screenName);

        request.setParam("count", getPageSize());

        // Get full tweet text.
        // See https://developer.twitter.com/en/docs/tweets/tweet-updates
        request.setParam("tweet_mode", "extended");

        // Exclude retweets
        request.setParam("include_rts", "false");
        // Exclude replies
        request.setParam("exclude_replies", "true");

        // Omit user info
        request.setParam("trim_user", "true");

        SpewResponse response = getConnection().request(request);

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

        public TweetIteratorBuilder(SpewConnection connection, Class<E> elementType) {
            super(connection, elementType, new TweetIterator<>());
        }

        public TweetIteratorBuilder<E> withScreenName(String screenName) {
            getIteratorInstance().screenName = screenName;
            return this;
        }
    }

}
