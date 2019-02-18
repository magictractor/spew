package uk.co.magictractor.oauth.flickr.processor;

import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.Tag;
import uk.co.magictractor.oauth.flickr.pojo.TagType;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Replace default titles from Digikam (based on the file name, "IMG_1234" etc)
 * with the name from the subject tag.
 */
public class TitleProcessor implements Processor<Photo, FlickrProcessorContext, FlickrPhotoChanges> {

	@Override
	public void process(FlickrPhotoChanges photoChanges, FlickrProcessorContext context) {
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
