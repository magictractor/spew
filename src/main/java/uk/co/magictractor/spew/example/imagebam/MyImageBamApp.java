package uk.co.magictractor.spew.example.imagebam;

import uk.co.magictractor.spew.api.OAuth1Application;
import uk.co.magictractor.spew.api.OAuth1ServiceProvider;
import uk.co.magictractor.spew.processor.properties.ResourceFileProperties;
import uk.co.magictractor.spew.provider.imagebam.ImageBam;

public class MyImageBamApp implements OAuth1Application {

    private final ResourceFileProperties properties = new ResourceFileProperties(MyImageBamApp.class);

    @Override
    public OAuth1ServiceProvider getServiceProvider() {
        return ImageBam.getInstance();
    }

    @Override
    public String getConsumerKey() {
        return properties.getProperty("consumer_key");
    }

    @Override
    public String getConsumerSecret() {
        return properties.getProperty("consumer_secret");
    }
}
