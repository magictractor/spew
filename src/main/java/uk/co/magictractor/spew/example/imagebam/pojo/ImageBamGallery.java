package uk.co.magictractor.spew.example.imagebam.pojo;

import com.google.common.base.MoreObjects;

public class ImageBamGallery {

    private String GID;
    private String URL;
    private String title;
    private String description;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("GID", GID)
                .add("title", title)
                .toString();
    }

}
