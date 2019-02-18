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
public class TagHierarchyProcessor implements Processor<Photo, FlickrProcessorContext, FlickrPhotoChanges> {

	@Override
	public void process(FlickrPhotoChanges photoChanges, FlickrProcessorContext context) {
		TagSet tagSet = photoChanges.getTagSet();

		for (TagType tagType : TagType.values()) {
			addTagHierarchy(tagType, tagSet);
		}
	}

	private void addTagHierarchy(TagType tagType, TagSet tagSet) {
		Tag deepestTag = tagSet.getDeepestTag(tagType);

		if (deepestTag == null) {
			// missing tag will be reported by TagCheckProcessor
			return;
		}

		Tag parentTag = deepestTag.getParent();
		while (parentTag != null) {
			tagSet.addTag(parentTag);
			parentTag = parentTag.getParent();
		}
	}

}
