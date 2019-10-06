package uk.co.magictractor.spew.example.twitter;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.provider.twitter.Twitter;

public class MyTwitterApp implements SpewOAuth1Application<Twitter> {

    private static final MyTwitterApp INSTANCE = SpewApplicationCache.add(MyTwitterApp.class);

    public static MyTwitterApp get() {
        return INSTANCE;
    }

    private MyTwitterApp() {
    }

    @Override
    public String toString() {
        return SpewApplication.toStringHelper(this).toString();
    }

}
