package uk.co.magictractor.oauth.processor.common;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.processor.DateAwareProcessorContext;

public class PhotoProcessorContext implements DateAwareProcessorContext<Photo, MutablePhoto> {

	private Set<Tag> unknownTags = new HashSet<>();

	@Override
	public MutablePhoto beforeElement(Photo photo) {
		return new MutablePhoto(photo);
	}

	@Override
	public void afterElement(MutablePhoto photo) {
	}

	@Override
	public LocalDate getDate(MutablePhoto photo) {
		return LocalDate.from(photo.originalDateTaken);
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
