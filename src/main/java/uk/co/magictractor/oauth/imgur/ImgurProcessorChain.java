package uk.co.magictractor.oauth.imgur;

import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.ProcessorChain;

public class ImgurProcessorChain extends ProcessorChain<FlickrPhoto, ImgurPhotoChanges, ImgurProcessorContext> {

	public ImgurProcessorChain() {
		// TODO! some of these processors could be used for multiple services

//		addProcessor(new TagFixProcessor("rgbe", "rbge"));
//		addProcessor(new TagFixProcessor("funghi", "fungi"));
//
//		addProcessor(new TagCheckProcessor());
//		addProcessor(new TagHierarchyProcessor());
//
//		// not default title
//		addProcessor(new TitleProcessor());

		// add "(record shot)" to title
		// upload date same as taken date
		// aliases for some tags (e.g. "gull" and "seagull", "RBGE" and "Botanics")
	}

	public static void main(String[] args) {
		ImgurProcessorChain processorChain = new ImgurProcessorChain();
		processorChain.execute(new ImgurPhotoIterator(), new ImgurProcessorContext());
	}

}
