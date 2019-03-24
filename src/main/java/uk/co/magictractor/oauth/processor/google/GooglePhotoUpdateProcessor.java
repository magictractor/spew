package uk.co.magictractor.oauth.processor.google;

import uk.co.magictractor.oauth.google.GoogleMediaItemIterator;
import uk.co.magictractor.oauth.processor.common.MutablePhoto;
import uk.co.magictractor.oauth.processor.common.PhotoProcessorContext;
import uk.co.magictractor.oauth.processor.common.PhotoTidyProcessorChain;
import uk.co.magictractor.oauth.processor.common.PhotoUpdateProcessor;

// TODO! currently just a stub
public class GooglePhotoUpdateProcessor extends PhotoUpdateProcessor {

	@Override
	public void process(MutablePhoto photo, PhotoProcessorContext context) {
//		if (photo.isTitleChanged()) {
//			setMeta(photo);
//		}
//
//		if (photo.isTagSetChanged()) {
//			setTags(photo);
//		}
		System.err.println("processing not done: " + photo);
	}

	public static void main(String[] args) {
		PhotoTidyProcessorChain processorChain = new PhotoTidyProcessorChain(new GooglePhotoUpdateProcessor());
		// TODO! should be passing in App to iterator (as done for OAuth2 iterators)
		processorChain.execute(new GoogleMediaItemIterator(), new PhotoProcessorContext());
	}

}
