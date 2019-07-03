package uk.co.magictractor.oauth.flickr.processor;

import java.util.Iterator;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.flickr.FlickrPhotoIterator.FlickrPhotoIteratorBuilder;
import uk.co.magictractor.oauth.flickr.MyFlickrApp;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.common.MutablePhoto;
import uk.co.magictractor.oauth.processor.common.PhotoProcessorContext;
import uk.co.magictractor.oauth.processor.common.PhotoUploadProcessor;
import uk.co.magictractor.oauth.processor.common.PhotoUploadProcessorChain;

/** Processor for uploading photos to a service provider for the first time. */
public class FlickrPhotoUploadProcessor extends PhotoUploadProcessor {

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (photo.getServiceProviderId() != null) {
            return;
        }
    }

    public static void main(String[] args) {
        PhotoUploadProcessorChain processorChain = new PhotoUploadProcessorChain(new FlickrPhotoUploadProcessor());
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyFlickrApp.class);
        Iterator<FlickrPhoto> iterator = new FlickrPhotoIteratorBuilder().withConnection(connection).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
