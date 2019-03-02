package uk.co.magictractor.oauth.processor.flickr;

import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 */
public class TagFixProcessor implements Processor<FlickrPhoto, MutablePhoto, FlickrProcessorContext> {

	private final Tag wrongTag;
	private final Tag rightTag;

	public TagFixProcessor(String wrongTag, String rightTag) {
		this.wrongTag = Tag.fetchOrCreateTag(wrongTag);
		this.rightTag = Tag.fetchTag(rightTag);
	}

	@Override
	public void process(MutablePhoto photoChanges, FlickrProcessorContext context) {
		if (photoChanges.getTagSet().getTags().contains(wrongTag)) {
			photoChanges.getTagSet().removeTag(wrongTag);
			photoChanges.getTagSet().addTag(rightTag);
		}
	}

}
