package uk.co.magictractor.spew.example.flickr;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.provider.flickr.Flickr;

public class MyFlickrApp implements SpewOAuth1Application<Flickr> {

    private static final MyFlickrApp INSTANCE = SpewApplicationCache.add(MyFlickrApp.class);

    public static MyFlickrApp get() {
        return INSTANCE;
    }

    private MyFlickrApp() {
    }

    @Override
    public String toString() {
        return SpewApplication.toStringHelper(this).toString();
    }

}
