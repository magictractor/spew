package uk.co.magictractor.oauth.imgur;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.Tag;
import uk.co.magictractor.oauth.processor.DateAwareProcessorContext;

public class ImgurProcessorContext implements DateAwareProcessorContext<Photo, ImgurPhotoChanges> {

	private Set<Tag> unknownTags = new HashSet<>();

	// TODO! this is the only change from the Flickr impl
	@Override
	public ImgurPhotoChanges beforeElement(Photo photo) {
		return new ImgurPhotoChanges(photo);
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
