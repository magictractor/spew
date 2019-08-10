package uk.co.magictractor.spew.example.imagebam;

import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.api.SpewOAuth1ServiceProvider;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;

public class MyImageBamApp implements SpewOAuth1Application {

    @Override
    public SpewOAuth1ServiceProvider getServiceProvider() {
        return ImageBam.getInstance();
    }

}
