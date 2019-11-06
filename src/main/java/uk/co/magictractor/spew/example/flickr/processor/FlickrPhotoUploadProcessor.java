package uk.co.magictractor.spew.example.flickr.processor;

import java.util.Iterator;

import uk.co.magictractor.spew.example.flickr.MyFlickrApp;
import uk.co.magictractor.spew.example.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.processor.common.MutableMedia;
import uk.co.magictractor.spew.processor.common.MediaProcessorContext;
import uk.co.magictractor.spew.processor.common.PhotoUploadProcessor;
import uk.co.magictractor.spew.processor.common.PhotoUploadProcessorChain;
import uk.co.magictractor.spew.provider.flickr.FlickrPhotoIterator.FlickrPhotoIteratorBuilder;

/** Processor for uploading photos to a service provider for the first time. */
public class FlickrPhotoUploadProcessor extends PhotoUploadProcessor {

    @Override
    public void process(MutableMedia photo, MediaProcessorContext context) {
        if (photo.getServiceProviderId() != null) {
            return;
        }
    }

    public static void main(String[] args) {
        PhotoUploadProcessorChain processorChain = new PhotoUploadProcessorChain(new FlickrPhotoUploadProcessor());
        Iterator<FlickrPhoto> iterator = new FlickrPhotoIteratorBuilder<>(MyFlickrApp.get(), FlickrPhoto.class).build();
        processorChain.execute(iterator, new MediaProcessorContext());
    }

}
