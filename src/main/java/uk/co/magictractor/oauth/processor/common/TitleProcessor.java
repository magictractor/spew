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

    private final TagType titleTagType;

    public TitleProcessor(String titleTagTypeName) {
        // TODO! if there's no tag with this name throw an error rather than creating it (or skip this processor)
        this.titleTagType = TagType.valueOf(titleTagTypeName);
    }

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (isDefaultTitle(photo.getTitle())) {
            String newTitle = createTitle(photo);

            if (newTitle != null) {
                photo.setTitle(newTitle);
            }
        }
    }

    protected String createTitle(Photo photo) {
        TagSet tagSet = photo.getTagSet();
        if (tagSet == null) {
            // TODO! log a warning (via context)
            System.err.println("No tags, so cannot create title");
            return null;
        }

        Tag subject = photo.getTagSet().getDeepestTag(titleTagType);
        if (subject == null) {
            // TODO! log a warning (via context)
            System.err.println("No subject tags, so cannot create title");
            return null;
        }
        if (subject.hasChildren()) {
            // TODO! log a warning (via context)
            System.err.println("Deepest subject tag has children, so not creating title");
            return null;
        }

        return createTitle(subject);
    }

    protected String createTitle(Tag tag) {
        String title = tag.getTagName();
        return title.substring(0, 1).toUpperCase() + title.substring(1);
    }

    protected boolean isDefaultTitle(String title) {
        // IMG_ for Canon Powershot SX60
        // _MG_ for Canon EOS 60D
        // PANA for Panasonic Lumix G9
        return Strings.isNullOrEmpty(title) || title.startsWith("IMG_") || title.startsWith("_MG_")
                || title.startsWith("PANA");
    }

}
