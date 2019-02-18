package uk.co.magictractor.oauth.flickr.pojo;

import java.util.Comparator;

public class TagComparator {
	
	private static final Comparator<Tag> TAG_TYPE_NULL_LAST = Comparator.comparing(t -> t.getTagType() == null);
	private static final Comparator<Tag> TAG_TYPE = Comparator.comparing(Tag::getTagType);
	private static final Comparator<Tag> DEPTH_ASC = Comparator.comparing(Tag::getDepth);
	private static final Comparator<Tag> DEPTH_DESC = DEPTH_ASC.reversed();

	// ah... nulls first is for the tag, not the type...
	public static final Comparator<Tag> ASCENDING = TAG_TYPE_NULL_LAST
			.thenComparing(TAG_TYPE)
			.thenComparing(DEPTH_DESC);

}
