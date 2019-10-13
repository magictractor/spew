package uk.co.magictractor.spew.provider.twitter;

import java.util.Iterator;

import uk.co.magictractor.spew.api.ApplicationRequest;
import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.example.twitter.MyTwitterApp;
import uk.co.magictractor.spew.example.twitter.pojo.TwitterUser;

// https://developer.twitter.com/en/docs/accounts-and-users/follow-search-get-users/api-reference/get-followers-list
public class TwitterFollowersIterator<E> extends TwitterCursorIterator<E> {

    private TwitterFollowersIterator() {
        super("users");
    }

    // If non-null fetch tweets for user with this screen name, otherwise tweets are fetched for the authenticated user.
    private String screenName;

    private boolean skipStatus = true;

    @Override
    protected ApplicationRequest createPageRequest() {
        ApplicationRequest request = createGetRequest("https://api.twitter.com/1.1/followers/list.json");

        request.setQueryStringParam("screen_name", screenName);

        request.setQueryStringParam("skipStatus", skipStatus);

        return request;
    }

    public static class TwitterFollowersIteratorBuilder<E>
            extends
            TwitterCursorIteratorBuilder<E, TwitterFollowersIterator<E>, TwitterFollowersIteratorBuilder<E>> {

        public TwitterFollowersIteratorBuilder(SpewApplication<?> application, Class<E> elementType) {
            super(application, elementType, new TwitterFollowersIterator<>());
        }

        public TwitterFollowersIteratorBuilder<E> withScreenName(String screenName) {
            getIteratorInstance().screenName = screenName;
            return this;
        }
    }

    public static void main(String[] args) {
        Iterator<TwitterUser> iter = new TwitterFollowersIteratorBuilder<>(MyTwitterApp.get(), TwitterUser.class)
                .build();
        while (iter.hasNext()) {
            TwitterUser follower = iter.next();
            System.err.println(follower);
        }
    }

}
