package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.Tag;
import uk.co.magictractor.spew.photo.TagComparator;

public class TagComparatorTest {

    @Test
    public void compare_subjectBeforeLocation() {
        Tag location = Tag.fetchTag("rbge");
        Tag subject = Tag.fetchTag("commondarter");

        List<Tag> tags = Arrays.asList(location, subject);
        tags.sort(TagComparator.ASCENDING);

        // assertThat(tags, Matchers.contains(subject, location));
        assertThat(tags).containsExactly(subject, location);
    }

    @Test
    public void compare_deepestFirst() {
        Tag odonata = Tag.fetchTag("odonata");
        Tag dragonfly = Tag.fetchTag("dragonfly");
        Tag commonDarter = Tag.fetchTag("commondarter");

        List<Tag> tags = Arrays.asList(dragonfly, commonDarter, odonata);
        tags.sort(TagComparator.ASCENDING);

        // assertThat(tags, Matchers.contains(commonDarter, dragonfly, odonata));
        assertThat(tags).containsExactly(commonDarter, dragonfly, odonata);
    }

    @Test
    public void compare_typeThenDepth() {
        Tag odonata = Tag.fetchTag("odonata");
        Tag dragonfly = Tag.fetchTag("dragonfly");
        Tag commonDarter = Tag.fetchTag("commondarter");
        Tag edinburgh = Tag.fetchTag("edinburgh");
        Tag rbge = Tag.fetchTag("rbge");

        List<Tag> tags = Arrays.asList(dragonfly, edinburgh, commonDarter, rbge, odonata);
        tags.sort(TagComparator.ASCENDING);

        // assertThat(tags, Matchers.contains(commonDarter, dragonfly, odonata, rbge,
        // edinburgh));
        assertThat(tags).containsExactly(commonDarter, dragonfly, odonata, rbge, edinburgh);
    }

    @Test
    public void compare_unknownTag() {
        Tag dragonfly = Tag.fetchTag("dragonfly");
        Tag unknown = Tag.fetchOrCreateTag("unknown");

        List<Tag> tags = Arrays.asList(unknown, dragonfly);
        tags.sort(TagComparator.ASCENDING);

        assertThat(tags).containsExactly(dragonfly, unknown);
    }

    @Test
    public void compare_multipleUnknownTags() {
        Tag unknown1 = Tag.fetchOrCreateTag("unknown one");
        Tag unknown2 = Tag.fetchOrCreateTag("unknown two");

        List<Tag> tags = Arrays.asList(unknown2, unknown1);
        tags.sort(TagComparator.ASCENDING);

        assertThat(tags).containsExactly(unknown1, unknown2);
    }
}
