package uk.co.magictractor.oauth.flickr.processor;

import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.Tag;
import uk.co.magictractor.oauth.flickr.pojo.TagType;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Ensure that parent tags are included too. For example for "common hawker"
 * ensure "dragonfly" and "odonata" tags are included.
 */
public class TagFixProcessor implements Processor<Photo, FlickrProcessorContext, FlickrPhotoChanges> {

	private final Tag wrongTag;
	private final Tag rightTag;

	public TagFixProcessor(String wrongTag, String rightTag) {
		this.wrongTag = Tag.fetchOrCreateTag(wrongTag);
		this.rightTag = Tag.fetchTag(rightTag);
	}

	@Override
	public void process(FlickrPhotoChanges photoChanges, FlickrProcessorContext context) {
		if (photoChanges.getTagSet().getTags().contains(wrongTag)) {
			photoChanges.getTagSet().removeTag(wrongTag);
			photoChanges.getTagSet().addTag(rightTag);
		}
	}

}
