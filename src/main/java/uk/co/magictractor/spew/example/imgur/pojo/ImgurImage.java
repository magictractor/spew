package uk.co.magictractor.spew.example.imgur.pojo;

import java.time.Instant;

import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.TagSet;

public class ImgurImage implements Photo {

    private String id;
    // Imgur uses this identifier (rather than id) when deleting images.
    private String deleteHash;
    private String title;
    private String description;
    // TODO! map from array in JSON
    // private String tags;
    private int width;
    private int height;
    private Instant datetime;

    @Override
    public String getServiceProviderId() {
        return id;
    }

    // TODO! use "name", which is the original file name, but with extension
    // stripped
    @Override
    public String getFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public TagSet getTagSet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Instant getDateTimeTaken() {
        return datetime;
    }

    @Override
    public Instant getDateTimeUpload() {
        // Imgur does not report the upload time.
        return null;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public Integer getHeight() {
        return height;
    }

}
