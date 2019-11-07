package uk.co.magictractor.spew.processor.common;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.photo.TagSet;

/**
 * It is expected that all other Media implementations will be immutable and any
 * changes made to images or sidecars will be done via this class.
 */
public class MutableMedia implements Media {

    // private final Photo photo;
    //	private final String photoId;
    //	private final String originalTitle;
    //	// TODO! private or bin
    //	public final LocalDate originalDateTaken;
    //	private final Instant originalDateTimeUpload;
    //	private final TagSet originalTagSet;
    private final Media originalMedia;

    private String title;
    // private Instant dateTimeUpload;
    private TagSet tagSet;

    public MutableMedia(Media media) {
        originalMedia = media;
        //		photoId = photo.getServiceProviderId();
        //		originalTitle = photo.getTitle();
        //		originalDateTaken = photo.getDateTaken();
        //		originalDateTimeUpload = photo.getDateTimeUpload();
        //		originalTagSet = photo.getTagSet();

        title = media.getTitle();
        // dateTimeUpload = originalDateTimeUpload;
        if (media.getTagSet() != null) {
            tagSet = new TagSet(media.getTagSet());
        }
    }

    // Mutable values

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTitleChanged() {
        return !Objects.equals(originalMedia.getTitle(), title);
    }

    // No setter, add and remove Tag instances this Tags instance
    // TODO! could ensure TagSet for other Photo impls is immutable
    @Override
    public TagSet getTagSet() {
        return tagSet;
    }

    public boolean isTagSetChanged() {
        return !Objects.equals(originalMedia.getTagSet(), tagSet);
    }

    // Immutable values

    @Override
    public String getServiceProviderId() {
        return originalMedia.getServiceProviderId();
    }

    @Override
    public String getFileName() {
        return originalMedia.getFileName();
    }

    @Override
    public String getDescription() {
        return originalMedia.getDescription();
    }

    @Override
    public Instant getDateTimeTaken() {
        return originalMedia.getDateTimeTaken();
    }

    @Override
    public Instant getDateTimeUpload() {
        return originalMedia.getDateTimeUpload();
    }

    @Override
    public Integer getWidth() {
        return originalMedia.getWidth();
    }

    @Override
    public Integer getHeight() {
        return originalMedia.getHeight();
    }

    @Override
    public String toString() {
        ToStringHelper toStringHelper = Media.toStringHelper(this);
        toStringHelper.add("title", getTitle());
        toStringHelper.add("dateTimeTaken", getDateTimeTaken());
        // TODO! would rather add underlying media type first but that's not supported by ToStringHelper
        toStringHelper.add("originalMedia.class", originalMedia.getClass().getSimpleName());
        toStringHelper.add("isTitleChanged", isTitleChanged());
        toStringHelper.add("isTagSetChanged", isTagSetChanged());
        return toStringHelper.toString();
    }
}
