package uk.co.magictractor.oauth.processor.common;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.common.TagType;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class TitleProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

	@Override
	public void process(MutablePhoto photoChanges, PhotoProcessorContext context) {
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
		// PANA for Panasonic Lumix G9
		return title.startsWith("IMG_") || title.startsWith("_MG_") || title.startsWith("PANA");
	}

}
