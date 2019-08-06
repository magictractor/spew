package uk.co.magictractor.spew.example.flickr;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.provider.flickr.Flickr;

public class MyFlickrApp implements OAuth1Application {

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return Flickr.getInstance();
    }

}
