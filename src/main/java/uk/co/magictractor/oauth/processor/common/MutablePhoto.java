package uk.co.magictractor.oauth.processor.common;

import java.time.Instant;
import java.util.Objects;

import com.google.common.base.MoreObjects.ToStringHelper;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

/**
 * It is expected that all other Photo implementations will be immutable and any
 * changes made to images or sidecars will be done via this class.
 */
public class MutablePhoto implements Photo {

    // private final Photo photo;
    //	private final String photoId;
    //	private final String originalTitle;
    //	// TODO! private or bin
    //	public final LocalDate originalDateTaken;
    //	private final Instant originalDateTimeUpload;
    //	private final TagSet originalTagSet;
    private final Photo originalPhoto;

    private String title;
    // private Instant dateTimeUpload;
    private TagSet tagSet;

    public MutablePhoto(Photo photo) {
        originalPhoto = photo;
        //		photoId = photo.getServiceProviderId();
        //		originalTitle = photo.getTitle();
        //		originalDateTaken = photo.getDateTaken();
        //		originalDateTimeUpload = photo.getDateTimeUpload();
        //		originalTagSet = photo.getTagSet();

        title = photo.getTitle();
        // dateTimeUpload = originalDateTimeUpload;
        if (photo.getTagSet() != null) {
            tagSet = new TagSet(photo.getTagSet());
        }
    }

    // Mutable values

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isTitleChanged() {
        return !Objects.equals(originalPhoto.getTitle(), title);
    }

    // No setter, add and remove Tag instances this Tags instance
    // TODO! could ensure TagSet for other Photo impls is immutable
    public TagSet getTagSet() {
        return tagSet;
    }

    public boolean isTagSetChanged() {
        return !Objects.equals(originalPhoto.getTagSet(), tagSet);
    }

    // Immutable values

    @Override
    public String getServiceProviderId() {
        return originalPhoto.getServiceProviderId();
    }

    @Override
    public String getFileName() {
        return originalPhoto.getFileName();
    }

    @Override
    public String getDescription() {
        return originalPhoto.getDescription();
    }

    @Override
    public Instant getDateTimeTaken() {
        return originalPhoto.getDateTimeTaken();
    }

    @Override
    public Instant getDateTimeUpload() {
        return originalPhoto.getDateTimeUpload();
    }

    @Override
    public Integer getWidth() {
        return originalPhoto.getWidth();
    }

    @Override
    public Integer getHeight() {
        return originalPhoto.getHeight();
    }

    public String toString() {
        ToStringHelper toStringHelper = Photo.toStringHelper(this);
        // TODO! would rather add underlying photo type first but that's not supported by ToStringHelper
        toStringHelper.add("originalPhoto.class", originalPhoto.getClass().getSimpleName());
        toStringHelper.add("isTitleChanged", isTitleChanged());
        toStringHelper.add("isTagSetChanged", isTagSetChanged());
        return toStringHelper.toString();
    }
}
