package uk.co.magictractor.spew.processor.common;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.photo.TagType;
import uk.co.magictractor.spew.processor.Processor;

/**
 * Replace default titles based on the file name, "IMG_1234" etc, with the name
 * from the subject tag.
 */
public class TitleProcessor implements Processor<Image, MutablePhoto, PhotoProcessorContext> {

    private final TagType titleTagType;

    private List<Function<Image, Boolean>> unwantedTitleFunctions = Arrays.asList(
        photo -> TitleProcessor.hasConsecutiveDigits(photo, 4),
        photo -> hasIncompleteTitle(photo));

    public TitleProcessor(String titleTagTypeName) {
        // TODO! if there's no tag with this name throw an error rather than creating it (or skip this processor)
        this.titleTagType = TagType.fetch(titleTagTypeName);
    }

    /**
     * Change the function which determines whether the current name of a photo
     * is a default, probably based on a file name, which should be replaced
     * with a better value. The better value is by default based on the deepest
     * subject tag.
     */
    public void setUnwantedTitleFunction(Function<Image, Boolean>... unwantedTitleFunctions) {
        this.unwantedTitleFunctions = Arrays.asList(unwantedTitleFunctions);
    }

    @Override
    public void process(MutablePhoto photo, PhotoProcessorContext context) {
        if (isUnwantedTitlePhoto(photo)) {
            String newTitle = createTitle(photo);

            if (newTitle != null) {
                photo.setTitle(newTitle);
            }
        }
    }

    private boolean isUnwantedTitlePhoto(Image photo) {
        return unwantedTitleFunctions.stream().anyMatch(func -> func.apply(photo));
    }

    protected String createTitle(Image photo) {
        TagSet tagSet = photo.getTagSet();
        if (tagSet == null) {
            // TODO! log a warning (via context)
            System.err.println("No tags, so cannot create title");
            return null;
        }

        Tag subject = getTitleTag(photo);
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

    private Tag getTitleTag(Image photo) {
        return photo.getTagSet().getDeepestTag(titleTagType);
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

    public static boolean hasConsecutiveDigits(Image photo, int digitCount) {
        String title = photo.getTitle();
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

    /**
     * @param photo
     * @return
     */
    public boolean hasIncompleteTitle(Image photo) {
        Tag titleTag = getTitleTag(photo);
        if (titleTag == null) {
            return false;
        }
        if (titleTag.getAliases().isEmpty()) {
            return false;
        }

        return photo.getTitle().equalsIgnoreCase(titleTag.getTagName())
                || photo.getTitle().equalsIgnoreCase(titleTag.getAliases().get(0));
    }

}
