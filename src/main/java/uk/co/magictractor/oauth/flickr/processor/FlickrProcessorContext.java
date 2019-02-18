package uk.co.magictractor.oauth.flickr.processor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.Tag;
import uk.co.magictractor.oauth.processor.DateAwareProcessorContext;

public class FlickrProcessorContext implements DateAwareProcessorContext<Photo, FlickrPhotoChanges> {

	private Set<Tag> unknownTags = new HashSet<>();

	@Override
	public FlickrPhotoChanges beforeElement(Photo photo) {
		return new FlickrPhotoChanges(photo);
	}

	@Override
	public LocalDate getDate(Photo photo) {
		return LocalDate.from(photo.dateTaken);
	}

	public void addUnknownTag(Tag tag) {
		unknownTags.add(tag);
	}

//	private List<Tag> getUnknownTags() {
//		return unknownTags.stream().sorted(Tag.TAG_NAME_COMPARATOR).collect(Collectors.toList());
//	}

	public void afterProcessing() {
		System.err.println("afterProcessing");

		if (!unknownTags.isEmpty()) {
			System.err.println("Unknown tags");
			unknownTags.stream().sorted(Tag.TAG_NAME_COMPARATOR).map(Tag::getTagName).forEach(System.err::println);
		}
	}
}
