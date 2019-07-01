package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.TagSet;
import uk.co.magictractor.oauth.common.TagType;

public class TagSetTest {

    @Test
    public void getDeepestTag() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        assertThat(tags.getDeepestTag(TagType.SUBJECT).getTagName()).isEqualTo("Four-spotted chaser");
    }

    @Test
    public void getDeepestTag_null() {
        TagSet tags = new TagSet("rbge");

        assertThat(tags.getDeepestTag(TagType.SUBJECT)).isNull();
    }

    @Test
    public void getQuotedTagNamesWithParents() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("fourspottedchaser
        // dragonfly odonata insect rbge edinburgh"));
        assertThat(tags.getQuotedTagNamesWithParents())
                .isEqualTo("\"Four-spotted chaser\" \"Dragonfly\" \"Odonata\" \"Insect\" \"RBGE\" \"Edinburgh\"");
    }

    @Test
    public void getQuotedTagNamesWithUnknownTag() {
        TagSet tags = new TagSet("rbge unknown");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("unknown rbge
        // edinburgh"));
        assertThat(tags.getQuotedTagNamesWithParents()).isEqualTo("\"unknown\" \"RBGE\" \"Edinburgh\"");
    }

}
