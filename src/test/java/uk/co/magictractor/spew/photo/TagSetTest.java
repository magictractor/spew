package uk.co.magictractor.spew.photo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TagSetTest {

    @Test
    public void testEquals_identical() {
        TagSet tagSet1 = new TagSet("otter edinburgh");
        TagSet tagSet2 = new TagSet("otter edinburgh");
        assertThat(tagSet1).isEqualTo(tagSet2);
        assertThat(tagSet2).isEqualTo(tagSet1);
    }

    @Test
    public void testEquals_same() {
        TagSet tagSet1 = new TagSet("Blackbird");
        TagSet tagSet2 = new TagSet("blackbird");
        assertThat(tagSet1).isEqualTo(tagSet2);
        assertThat(tagSet2).isEqualTo(tagSet1);
    }

    @Test
    public void testEquals_extraElement() {
        TagSet tagSet1 = new TagSet("starling edinburgh");
        TagSet tagSet2 = new TagSet("starling bird edinburgh");
        assertThat(tagSet1).isNotEqualTo(tagSet2);
        assertThat(tagSet2).isNotEqualTo(tagSet1);
    }

    @Test
    public void testEquals_differentOrder() {
        TagSet tagSet1 = new TagSet("otter edinburgh");
        TagSet tagSet2 = new TagSet("edinburgh otter");
        assertThat(tagSet1).isNotEqualTo(tagSet2);
        assertThat(tagSet2).isNotEqualTo(tagSet1);
    }

    /*
     * Note that the tags returned by the Flickr API are always in lower case.
     */
    @Test
    public void testUnknownTag_casePreserved() {
        TagSet tagSet = new TagSet("rec:grid=NT123456");
        List<Tag> tags = new ArrayList<>(tagSet.getTags());
        assertThat(tags).hasSize(1);
        assertThat(tags.get(0).getTagName()).isEqualTo("rec:grid=NT123456");
    }

    @Test
    public void getDeepestTag() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        assertThat(tags.getDeepestTag("SUBJECT").getTagName()).isEqualTo("four-spotted chaser");
    }

    @Test
    public void getDeepestTag_null() {
        TagSet tags = new TagSet("rbge");

        assertThat(tags.getDeepestTag("SUBJECT")).isNull();
    }

}
