package uk.co.magictractor.spew.twitter.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.api.SpewConnection;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.processor.SimpleProcessor;
import uk.co.magictractor.spew.processor.SimpleProcessorContext;
import uk.co.magictractor.spew.twitter.MyTwitterApp;
import uk.co.magictractor.spew.twitter.TweetIterator.TweetIteratorBuilder;
import uk.co.magictractor.spew.twitter.pojo.Tweet;

public class TweetProcessor implements SimpleProcessor<Tweet> {

    @Override
    public void process(Tweet element, SimpleProcessorContext<Tweet> context) {
        // TODO Auto-generated method stub
    }

    public static void main(String[] args) {
        SpewConnection connection = OAuthConnectionFactory.getConnection(MyTwitterApp.class);
        Iterator<Tweet> iter = new TweetIteratorBuilder(connection).build();
        while (iter.hasNext()) {
            Tweet tweet = iter.next();
            if (tweet.getLikes() >= 100) {
                System.out.println(tweet);
            }
        }
    }

}
