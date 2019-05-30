package uk.co.magictractor.oauth.twitter;

import java.util.List;

import com.jayway.jsonpath.TypeRef;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageTokenServiceIterator;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.twitter.pojo.Tweet;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class TweetIterator extends PageTokenServiceIterator<Tweet> {

    public TweetIterator(OAuthConnection connection) {
        super(connection);
    }

    @Override
    protected PageAndNextToken<Tweet> fetchPage(String pageToken) {
        OAuthRequest request = OAuthRequest
                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

        request.setParam("max_id", pageToken);

        // temp
        request.setParam("count", 10);

        // Get full tweet text.
        // See https://developer.twitter.com/en/docs/tweets/tweet-updates
        request.setParam("tweet_mode", "extended");

        // Exclude retweets
        request.setParam("include_rts", "false");
        // Exclude replies
        request.setParam("exclude_replies", "true");

        // Omit user info
        request.setParam("trim_user", "true");

        OAuthResponse response = getConnection().request(request);

        List<Tweet> page = response.getObject("$", new TypeRef<List<Tweet>>() {
        });

        // TODO! where is the token in the response??
        // since_id is to get most recent (e.g. check for new tweets)
        String nextToken;
        if (!page.isEmpty()) {
            Tweet lastTweet = page.get(page.size() - 1);
            nextToken = Long.toString(lastTweet.getId() - 1);
        }
        else {
            nextToken = null;
        }

        return new PageAndNextToken<>(page, nextToken);
    }

    // TODO! temp
    public static void main(String[] args) {
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyTwitterApp.class);
        TweetIterator iter = new TweetIterator(connection);
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            if (tweet.getLikes() >= 100) {
                System.out.println(tweet);
            }
        }
    }

}
