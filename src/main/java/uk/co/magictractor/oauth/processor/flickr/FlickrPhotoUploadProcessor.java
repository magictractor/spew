package uk.co.magictractor.oauth.processor.flickr;

import uk.co.magictractor.oauth.flickr.FlickrPhotoIterator;
import uk.co.magictractor.oauth.flickr.MyFlickrApp;
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
		processorChain.execute(new FlickrPhotoIterator(MyFlickrApp.getInstance()), new PhotoProcessorContext());
	}

}
