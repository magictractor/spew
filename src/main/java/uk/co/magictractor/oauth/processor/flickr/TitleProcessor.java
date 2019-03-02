package uk.co.magictractor.oauth.processor.flickr;

import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.common.TagType;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class TitleProcessor implements Processor<FlickrPhoto, MutablePhoto, FlickrProcessorContext> {

	@Override
	public void process(MutablePhoto photoChanges, FlickrProcessorContext context) {
		if (isDefaultTitle(photoChanges.getTitle())) {
			Tag subject = photoChanges.getTagSet().getDeepestTag(TagType.SUBJECT);
			if (subject != null && !subject.hasChildren()) {
				photoChanges.setTitle(subject.getTagName());
			}
		}
	}

	private boolean isDefaultTitle(String title) {
		// IMG_ for Canon Powershot SX60
		// _MG_ for Canon EOS 60D
		return title.startsWith("IMG_") || title.startsWith("_MG_");
	}

}
