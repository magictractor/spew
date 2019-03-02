package uk.co.magictractor.oauth.imgur;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import uk.co.magictractor.oauth.common.Tag;
import uk.co.magictractor.oauth.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.oauth.processor.DateAwareProcessorContext;

// TODO! use MutablePhoto rather than ImgurPhotoChanges
public class ImgurProcessorContext implements DateAwareProcessorContext<FlickrPhoto, ImgurPhotoChanges> {

	private Set<Tag> unknownTags = new HashSet<>();

	// TODO! this is the only change from the Flickr impl
	@Override
	public ImgurPhotoChanges beforeElement(FlickrPhoto photo) {
		return new ImgurPhotoChanges(photo);
	}
	

	@Override
	public void afterElement(ImgurPhotoChanges photo) {
		// TODO! move persist code
		// probably have a processor at the end of the chain which deals with it,
		// keeping MutablePhoto common, tag/title tidying common, and just one different
		// processor at the end of the chain for updating Flickr/Imgur/Google Photos.
		photo.persist();
	}

	@Override
	public LocalDate getDate(ImgurPhotoChanges photo) {
		// return LocalDate.from(photo.dateTaken);
		throw new UnsupportedOperationException("use MutablePhoto rather than ImgurPhotoChanges");
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
