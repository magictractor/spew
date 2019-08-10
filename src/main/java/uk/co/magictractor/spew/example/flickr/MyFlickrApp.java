package uk.co.magictractor.spew.example.flickr;

import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.provider.flickr.Flickr;

public class MyFlickrApp implements SpewOAuth1Application {

    @Override
    public SpewOAuth1ServiceProvider getServiceProvider() {
        return Flickr.getInstance();
    }

}
