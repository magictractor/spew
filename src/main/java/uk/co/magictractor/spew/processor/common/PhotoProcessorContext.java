package uk.co.magictractor.spew.processor.common;

import java.util.Collection;
import java.util.LinkedHashMap;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.processor.ProcessorContext;

public class PhotoProcessorContext implements ProcessorContext<Photo, MutablePhoto> {

    private LinkedHashMap<String, MutableAlbum> albums = new LinkedHashMap<>();

    @Override
    public MutablePhoto beforeElement(Photo photo) {
        return new MutablePhoto(photo);
    }

    @Override
    public void afterElement(MutablePhoto photo) {
    }

    public MutableAlbum getAlbum(String albumTitle) {
        MutableAlbum album = albums.get(albumTitle);
        if (album == null) {
            album = new MutableAlbum(albumTitle);
            albums.put(albumTitle, album);
        }
        return album;
    }

    public boolean hasAlbum(String title) {
        return albums.containsKey(title);
    }

    public Collection<MutableAlbum> getAlbums() {
        return albums.values();
    }

}
