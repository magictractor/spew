package uk.co.magictractor.oauth.common;

import java.util.Comparator;

public class TagComparator {

    private static final Comparator<Tag> TAG_TYPE = Comparator.comparing(Tag::getTagType,
        Comparator.nullsLast(Comparator.naturalOrder()));
    private static final Comparator<Tag> DEPTH_ASC = Comparator.comparing(Tag::getDepth);
    private static final Comparator<Tag> DEPTH_DESC = DEPTH_ASC.reversed();
    private static final Comparator<Tag> NAME_ASC = Comparator.comparing(Tag::getTagName);

    public static final Comparator<Tag> ASCENDING = TAG_TYPE.thenComparing(DEPTH_DESC).thenComparing(NAME_ASC);

}
