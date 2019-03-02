package uk.co.magictractor.oauth.processor.flickr;

import uk.co.magictractor.oauth.flickr.FlickrPhotoIterator;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.ProcessorChain;

// Tidies tags, descriptions etc for photos already uploaded to Flickr.
public class FlickrProcessorChain extends ProcessorChain<FlickrPhoto, MutablePhoto, FlickrProcessorContext> {

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
