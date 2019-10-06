package uk.co.magictractor.spew.example.imgur;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth2Application;
import uk.co.magictractor.spew.provider.imgur.Imgur;

public class MyImgurApp implements SpewOAuth2Application<Imgur> {

    private static final MyImgurApp INSTANCE = SpewApplicationCache.add(MyImgurApp.class);

    public static MyImgurApp get() {
        return INSTANCE;
    }

    private MyImgurApp() {
    }

    @Override
    public String toString() {
        return SpewApplication.toStringHelper(this).toString();
    }

}
