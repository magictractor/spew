package uk.co.magictractor.oauth.twitter.processor;

import java.util.Iterator;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.processor.SimpleProcessor;
import uk.co.magictractor.oauth.processor.SimpleProcessorContext;
import uk.co.magictractor.oauth.twitter.MyTwitterApp;
import uk.co.magictractor.oauth.twitter.TweetIterator.TweetIteratorBuilder;
import uk.co.magictractor.oauth.twitter.pojo.Tweet;

public class TweetProcessor implements SimpleProcessor<Tweet> {

    @Override
    public void process(Tweet element, SimpleProcessorContext<Tweet> context) {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) {
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyTwitterApp.class);
        Iterator<Tweet> iter = new TweetIteratorBuilder().withConnection(connection).build();
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            if (tweet.getLikes() >= 100) {
                System.out.println(tweet);
            }
        }
    }

}
