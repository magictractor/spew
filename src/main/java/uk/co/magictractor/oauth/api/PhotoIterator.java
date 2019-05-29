package uk.co.magictractor.oauth.api;

import java.util.Collection;
import java.util.Iterator;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.filter.DateTakenPhotoFilter;
import uk.co.magictractor.oauth.common.filter.DateUploadedPhotoFilter;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;

public interface PhotoIterator<P extends Photo> extends Iterator<P> {

    default void addFilter(PhotoFilter filter) {
        if (!supportsFilter(filter)) {
            throw new UnsupportedOperationException("Filter type " + filter.getClass().getSimpleName()
                    + " is not supported. Use PhotoIterator.builder().addFilter() to apply the filter instead.");
        }

        if (filter instanceof DateTakenPhotoFilter) {
            setDateTakenPhotoFilter((DateTakenPhotoFilter) filter);
        }
        else if (filter instanceof DateUploadedPhotoFilter) {
            setDateUploadedPhotoFilter((DateUploadedPhotoFilter) filter);
        }
        else {
            throw new UnsupportedOperationException(
                "Code must be modified to handle filter type " + filter.getClass().getSimpleName());
        }
    }

    default PhotoIteratorBuilder<P> builder() {
        return new PhotoIteratorBuilder<>(this);
    }

    default boolean supportsFilter(PhotoFilter filter) {
        return supportedPhotoFilters().contains(filter.getClass());
    }

    /**
     * This method should generally not be called directly, TODO... (and copy
     * below).
     */
    default void setDateTakenPhotoFilter(DateTakenPhotoFilter filter) {
        throw new UnsupportedOperationException(
            "setDateTakenPhotoFilter() must be overridden when DateTakenPhotoFilter is supported");
    }

    default void setDateUploadedPhotoFilter(DateUploadedPhotoFilter filter) {
        throw new UnsupportedOperationException(
            "setDateUploadedPhotoFilter() must be overridden when DateUploadedPhotoFilter is supported");
    }

    public abstract Collection<Class<? extends PhotoFilter>> supportedPhotoFilters();

}
