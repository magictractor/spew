package uk.co.magictractor.spew.photo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class TagSetTest {

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

    @Test
    public void getQuotedTagNamesWithParents() {
        TagSet tags = new TagSet("rbge fourspottedchaser");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("fourspottedchaser
        // dragonfly odonata insect rbge edinburgh"));
        assertThat(tags.getQuotedTagNamesWithAliasesAndParents())
                .isEqualTo(
                    "\"four-spotted chaser\" \"Libellula quadrimaculata\" \"dragonfly\" \"odonata\" \"insect\" \"RBGE\" \"Botanics\" \"Royal Botanic Garden Edinburgh\" \"Edinburgh\"");
    }

    @Test
    public void getQuotedTagNamesWithUnknownTag() {
        TagSet tags = new TagSet("rbge unknown");

        // assertThat(tags.getCompactTagNamesWithParents(), equalTo("unknown rbge
        // edinburgh"));
        assertThat(tags.getQuotedTagNamesWithAliasesAndParents())
                .isEqualTo("\"RBGE\" \"Botanics\" \"Royal Botanic Garden Edinburgh\" \"Edinburgh\" \"unknown\"");
    }

}
