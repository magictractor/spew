package uk.co.magictractor.spew.processor.common;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.common.Tag;
import uk.co.magictractor.spew.processor.DateAwareProcessorContext;

public class PhotoProcessorContext implements DateAwareProcessorContext<Photo, MutablePhoto> {

    private Set<Tag> unknownTags = new HashSet<>();
    private LinkedHashMap<String, MutableAlbum> albums = new LinkedHashMap<>();

    @Override
    public MutablePhoto beforeElement(Photo photo) {
        return new MutablePhoto(photo);
    }

    @Override
    public void afterElement(MutablePhoto photo) {
    }

    @Override
    public LocalDate getDate(MutablePhoto photo) {
        return LocalDate.from(photo.getDateTaken());
    }

    public void addUnknownTag(Tag tag) {
        unknownTags.add(tag);
    }

    //	private List<Tag> getUnknownTags() {
    //		return unknownTags.stream().sorted(Tag.TAG_NAME_COMPARATOR).collect(Collectors.toList());
    //	}

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

    // TODO! move to TagCheckProcessor
    @Override
    public void afterProcessing() {
        System.err.println("afterProcessing");

        if (!unknownTags.isEmpty()) {
            System.err.println("Unknown tags");
            unknownTags.stream().sorted(Tag.TAG_NAME_COMPARATOR).map(Tag::getTagName).forEach(System.err::println);
        }
    }

}
