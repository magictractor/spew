package uk.co.magictractor.spew.imagebam;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;

public class MyImageBamApp implements OAuth1Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyImageBamApp.class);

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return ImageBam.getInstance();
    }

    @Override
    public String getAppToken() {
        return properties.getProperty("app_token");
    }

    @Override
    public String getAppSecret() {
        return properties.getProperty("app_secret");
    }
}
