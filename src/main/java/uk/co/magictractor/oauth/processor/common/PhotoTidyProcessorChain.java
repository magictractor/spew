package uk.co.magictractor.oauth.processor.common;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.processor.ProcessorChain;

// Tidies tags, descriptions etc for photos already uploaded to Flickr.
public class PhotoTidyProcessorChain extends ProcessorChain<Photo, MutablePhoto, PhotoProcessorContext> {

	public PhotoTidyProcessorChain(PhotoUpdateProcessor persistProcessor) {
		//addProcessor(new TagFixProcessor("rgbe", "rbge"));
		//addProcessor(new TagFixProcessor("funghi", "fungi"));

		addProcessor(new TagCheckProcessor());
		addProcessor(new TagHierarchyProcessor());

		// not default title
		addProcessor(new TitleProcessor());

		// add "(record shot)" to title
		// upload date same as taken date
		// aliases for some tags (e.g. "gull" and "seagull", "RBGE" and "Botanics")
		
		addProcessor(persistProcessor);
	}

}
