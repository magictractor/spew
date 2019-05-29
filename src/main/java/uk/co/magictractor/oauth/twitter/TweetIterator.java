package uk.co.magictractor.oauth.twitter;

import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageTokenServiceIterator;
import uk.co.magictractor.oauth.twitter.pojo.Tweet;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class TweetIterator extends PageTokenServiceIterator<Tweet> {

    @Override
    protected PageAndNextToken<Tweet> fetchPage(String pageToken) {
        OAuthRequest request = OAuthRequest
                .createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

        // See also GoogleServiceIterator
        // TODO! must not have hard coded app in the middle of the iterator!

        // TODO! something like OAuthConnectionFactory.getConnection(MyApp.class).request()

        OAuthResponse response = MyTwitterApp.getInstance().getConnection().request(request);

        return null;
    }

    // TODO! temp
    public static void main(String[] args) {
        new TweetIterator().next();
    }

}
