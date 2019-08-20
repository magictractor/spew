package uk.co.magictractor.spew.example.imagebam;

import uk.co.magictractor.spew.api.SpewOAuth1Application;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;

public class MyImageBamApp implements SpewOAuth1Application<ImageBam> {

    @Override
    public ImageBam getServiceProvider() {
        return ImageBam.getInstance();
    }

}
