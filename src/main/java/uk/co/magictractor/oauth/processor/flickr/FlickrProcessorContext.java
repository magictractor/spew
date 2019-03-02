package uk.co.magictractor.oauth.processor.flickr;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.DateAwareProcessorContext;

public class FlickrProcessorContext implements DateAwareProcessorContext<FlickrPhoto, MutablePhoto> {

	private Set<Tag> unknownTags = new HashSet<>();

	@Override
	public MutablePhoto beforeElement(FlickrPhoto photo) {
		return new MutablePhoto(photo);
	}

	@Override
	public void afterElement(MutablePhoto photo) {
		photo.persist();
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
