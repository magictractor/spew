package uk.co.magictractor.spew.example.flickr;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;
import uk.co.magictractor.spew.provider.flickr.Flickr;

public class MyFlickrApp implements OAuth1Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyFlickrApp.class);

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return Flickr.getInstance();
    }

    @Override
    public String getConsumerKey() {
        return properties.getProperty("consumer_key");
    }

    @Override
    public String getConsumerSecret() {
        return properties.getProperty("consumer_secret");
    }
}
