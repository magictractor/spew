package uk.co.magictractor.spew.provider.twitter;

import java.util.Iterator;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.example.twitter.MyTwitterApp;
import uk.co.magictractor.spew.example.twitter.pojo.Tweet;

// https://developer.twitter.com/en/docs/tweets/timelines/api-reference/get-statuses-user_timeline.html
public class TwitterUserTimelineIterator<E> extends TwitterTimelineIterator<E> {

    // If non-null fetch tweets for user with this screen name, otherwise tweets are fetched for the authenticated user.
    private String screenName;

    @Override
    protected ApplicationRequest createPageRequest() {
        ApplicationRequest request = createGetRequest("https://api.twitter.com/1.1/statuses/user_timeline.json");

        request.setQueryStringParam("screen_name", screenName);

        // Get full tweet text.
        // See https://developer.twitter.com/en/docs/tweets/tweet-updates
        request.setQueryStringParam("tweet_mode", "extended");

        // Exclude retweets
        request.setQueryStringParam("include_rts", "false");
        // Exclude replies
        request.setQueryStringParam("exclude_replies", "true");

        // Omit user info
        request.setQueryStringParam("trim_user", "true");

        return request;
    }

    public static class UserTimelineIteratorBuilder<E>
            extends TwitterTimelineIteratorBuilder<E, TwitterUserTimelineIterator<E>, UserTimelineIteratorBuilder<E>> {

        public UserTimelineIteratorBuilder(SpewApplication<?> application, Class<E> elementType) {
            super(application, elementType, new TwitterUserTimelineIterator<>());
        }

        public UserTimelineIteratorBuilder<E> withScreenName(String screenName) {
            getIteratorInstance().screenName = screenName;
            return this;
        }
    }

    public static void main(String[] args) {
        Iterator<Tweet> iter = new UserTimelineIteratorBuilder<>(MyTwitterApp.get(), Tweet.class)
                // .withFilter(new DateTakenPhotoFilter(DateRange.forMonth(2019, 2)))
                .build();
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            System.err.println(tweet);
        }
    }

}
