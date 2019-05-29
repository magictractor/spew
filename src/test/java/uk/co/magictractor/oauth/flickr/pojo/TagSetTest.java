package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.common.TagType;

public class TagSetTest {

    @Test
    public void getDeepestTag() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        assertThat(tags.getDeepestTag(TagType.SUBJECT).getCompactTagName()).isEqualTo("fourspottedchaser");
    }

    @Test
    public void getDeepestTag_null() {
        TagSet tags = new TagSet("rbge");

        assertThat(tags.getDeepestTag(TagType.SUBJECT)).isNull();
    }

    @Test
    public void getCompactTagNamesWithParents() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("fourspottedchaser
        // dragonfly odonata insect rbge edinburgh"));
        assertThat(tags.getCompactTagNamesWithParents())
                .isEqualTo("fourspottedchaser dragonfly odonata insect rbge edinburgh");
    }

    @Test
    public void getCompactTagNamesWithUnknownTag() {
        TagSet tags = new TagSet("rbge unknown");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("unknown rbge
        // edinburgh"));
        assertThat(tags.getCompactTagNamesWithParents()).isEqualTo("unknown rbge edinburgh");
    }

}
