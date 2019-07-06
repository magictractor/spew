package uk.co.magictractor.spew.processor.common;

import java.util.function.Function;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.common.Tag;
import uk.co.magictractor.spew.common.TagSet;
import uk.co.magictractor.spew.common.TagType;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class TitleProcessor implements Processor<Photo, MutablePhoto, PhotoProcessorContext> {

    private final TagType titleTagType;

    private Function<String, Boolean> defaultTitleFunction = (title) -> TitleProcessor.hasConsecutiveDigits(title, 4);

    public TitleProcessor(String titleTagTypeName) {
        // TODO! if there's no tag with this name throw an error rather than creating it (or skip this processor)
        this.titleTagType = TagType.valueOf(titleTagTypeName);
    }

    /**
     * Change the function which determines whether the current name of a photo
     * is a default, probably based on a file name, which should be replaced
     * with a better value. The better value is by default based on the deepest
     * subject tag.
     */
    public void setDefaultTitleFunction(Function<String, Boolean> defaultTitleFunction) {
        this.defaultTitleFunction = defaultTitleFunction;
    }

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (defaultTitleFunction.apply(photo.getTitle())) {
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

    // TODO! this is very specific to "common name (scientific name)"
    protected String createTitle(Tag tag) {
        String title = tag.getTagName();
        //return title.substring(0, 1).toUpperCase() + title.substring(1);

        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append(title.substring(0, 1).toUpperCase());
        titleBuilder.append(title.substring(1));

        if (!tag.getAliases().isEmpty()) {
            titleBuilder.append(" (");
            titleBuilder.append(tag.getAliases().get(0));
            titleBuilder.append(")");
        }

        return titleBuilder.toString();
    }

    public static boolean hasConsecutiveDigits(String title, int digitCount) {
        int consecutiveDigits = 0;
        for (int i = 0; i < title.length(); i++) {
            if (Character.isDigit(title.charAt(i))) {
                if (++consecutiveDigits == digitCount) {
                    return true;
                }
            }
            else {
                consecutiveDigits = 0;
            }
        }

        return false;
    }

}
