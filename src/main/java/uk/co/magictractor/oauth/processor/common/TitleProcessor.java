package uk.co.magictractor.oauth.processor.common;

import com.google.common.base.Strings;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.common.TagType;
import uk.co.magictractor.oauth.processor.Processor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class TitleProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (isDefaultTitle(photo.getTitle())) {
            TagSet tagSet = photo.getTagSet();
            if (tagSet == null) {
                // TODO! log a warning (via context)
                System.err.println("No tags, so cannot create title");
                return;
            }

            Tag subject = photo.getTagSet().getDeepestTag(TagType.SUBJECT);
            if (subject != null && !subject.hasChildren()) {
                photo.setTitle(subject.getTagName());
            }
        }
    }

    private boolean isDefaultTitle(String title) {
        // IMG_ for Canon Powershot SX60
        // _MG_ for Canon EOS 60D
        // PANA for Panasonic Lumix G9
        return Strings.isNullOrEmpty(title) || title.startsWith("IMG_") || title.startsWith("_MG_")
                || title.startsWith("PANA");
    }

}
