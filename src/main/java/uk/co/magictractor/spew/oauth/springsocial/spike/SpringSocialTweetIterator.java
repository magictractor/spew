package uk.co.magictractor.spew.oauth.springsocial.spike;

import java.util.Iterator;

import uk.co.magictractor.spew.api.OAuthConnection;
import uk.co.magictractor.spew.api.OAuthRequest;
import uk.co.magictractor.spew.api.OAuthResponse;
import uk.co.magictractor.spew.api.PageTokenServiceIterator;
import uk.co.magictractor.spew.twitter.MyTwitterApp;
import uk.co.magictractor.spew.twitter.pojo.Tweet;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class SpringSocialTweetIterator extends PageTokenServiceIterator<Tweet> {

    // If non-null fetch tweets for user with this screen name, otherwise tweets are fetched for the authenticated user.
    private String screenName;

    private SpringSocialTweetIterator() {
    }

    public SpringSocialTweetIterator withScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    @Override
    protected PageAndNextToken<Tweet> fetchPage(String pageToken) {
        OAuthRequest request = OAuthRequest
                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

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

        OAuthResponse response = getConnection().request(request);

        return null;
    }

    //    @Override
    //    protected PageAndNextToken<Tweet> fetchPage(String pageToken) {
    //        OAuthRequest request = OAuthRequest
    //                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");
    //
    //        request.setParam("max_id", pageToken);
    //
    //        request.setParam("screen_name", screenName);
    //
    //        request.setParam("count", getPageSize());
    //
    //        // Get full tweet text.
    //        // See https://developer.twitter.com/en/docs/tweets/tweet-updates
    //        request.setParam("tweet_mode", "extended");
    //
    //        // Exclude retweets
    //        request.setParam("include_rts", "false");
    //        // Exclude replies
    //        request.setParam("exclude_replies", "true");
    //
    //        // Omit user info
    //        request.setParam("trim_user", "true");
    //
    //        OAuthResponse response = getConnection().request(request);
    //
    //        List<Tweet> page = response.getObject("$", new TypeRef<List<Tweet>>() {
    //        });
    //
    //        // TODO! where is the token in the response??
    //        // since_id is to get most recent (e.g. check for new tweets)
    //        String nextToken;
    //        if (!page.isEmpty()) {
    //            Tweet lastTweet = page.get(page.size() - 1);
    //            nextToken = Long.toString(lastTweet.getId() - 1);
    //        }
    //        else {
    //            nextToken = null;
    //        }
    //
    //        return new PageAndNextToken<>(page, nextToken);
    //    }

    public static class TweetIteratorBuilder
            extends PageTokenServiceIteratorBuilder<Tweet, SpringSocialTweetIterator, TweetIteratorBuilder> {

        public TweetIteratorBuilder(OAuthConnection connection) {
            super(connection, new SpringSocialTweetIterator());
        }
    }

    public static void main(String[] args) {
        OAuthConnection connection = new SpringSocialOAuth1Connection(new MyTwitterApp());
        Iterator<Tweet> iter = new TweetIteratorBuilder(connection).build();
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            if (tweet.getLikes() >= 100) {
                System.out.println(tweet);
            }
        }
    }

}
