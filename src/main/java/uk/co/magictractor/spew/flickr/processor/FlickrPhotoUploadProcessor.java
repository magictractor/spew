package uk.co.magictractor.spew.flickr.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.api.OAuthConnection;
import uk.co.magictractor.spew.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.spew.flickr.MyFlickrApp;
import uk.co.magictractor.spew.flickr.FlickrPhotoIterator.FlickrPhotoIteratorBuilder;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.processor.common.MutablePhoto;
import uk.co.magictractor.spew.processor.common.PhotoProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoUploadProcessor;
import uk.co.magictractor.spew.processor.common.PhotoUploadProcessorChain;

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
        Iterator<FlickrPhoto> iterator = new FlickrPhotoIteratorBuilder(connection).build();
        processorChain.execute(iterator, new PhotoProcessorContext());
    }

}
