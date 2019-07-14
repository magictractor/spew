package uk.co.magictractor.spew.common;

public interface Album {

    String getServiceProviderId();

    String getTitle();

    String getAlbumUrl();

    // Some service provides (such as Google Photos) require additional values to be
    // added to this to specify the size of the cover photo.
    String getCoverPhotoBaseUrl();

    int getPhotoCount();
}
