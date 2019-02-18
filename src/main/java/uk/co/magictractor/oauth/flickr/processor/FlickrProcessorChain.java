package uk.co.magictractor.oauth.flickr.processor;

import uk.co.magictractor.oauth.flickr.FlickrPhotoIterator;
import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.processor.ProcessorChain;

public class FlickrProcessorChain extends ProcessorChain<Photo, FlickrProcessorContext, FlickrPhotoChanges> {

	public FlickrProcessorChain() {
		addProcessor(new TagFixProcessor("rgbe", "rbge"));
		addProcessor(new TagFixProcessor("funghi", "fungi"));

		addProcessor(new TagCheckProcessor());
		addProcessor(new TagHierarchyProcessor());

		// not default title
		addProcessor(new TitleProcessor());

		// add "(record shot)" to title
		// upload date same as taken date
		// aliases for some tags (e.g. "gull" and "seagull", "RBGE" and "Botanics")
	}

	public static void main(String[] args) {
		FlickrProcessorChain processorChain = new FlickrProcessorChain();
		processorChain.execute(new FlickrPhotoIterator(), new FlickrProcessorContext());
	}

}
