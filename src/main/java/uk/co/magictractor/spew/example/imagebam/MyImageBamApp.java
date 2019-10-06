package uk.co.magictractor.spew.example.imagebam;

import uk.co.magictractor.spew.api.SpewApplication;
import uk.co.magictractor.spew.api.SpewApplicationCache;
import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;

public class MyImageBamApp implements SpewOAuth1Application<ImageBam> {

    private static final MyImageBamApp INSTANCE = SpewApplicationCache.add(MyImageBamApp.class);

    public static MyImageBamApp get() {
        return INSTANCE;
    }

    private MyImageBamApp() {
    }

    @Override
    public String toString() {
        return SpewApplication.toStringHelper(this).toString();
    }

}
