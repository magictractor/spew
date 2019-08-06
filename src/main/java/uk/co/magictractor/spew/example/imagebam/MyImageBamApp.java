package uk.co.magictractor.spew.example.imagebam;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;

public class MyImageBamApp implements OAuth1Application {

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return ImageBam.getInstance();
    }

}
