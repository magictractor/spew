package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.Tag;

public class TagTest {

    @Test
    public void fetchTag() {
        Tag tag = Tag.fetchTag("fourspottedchaser");

        //		assertThat(tag, notNullValue());
        //		assertThat(tag.getCompactTagName(), equalTo("fourspottedchaser"));
        //		assertThat(tag.getTagName(), equalTo("Four-spotted chaser"));

        assertThat(tag).isNotNull();
        assertThat(tag.getCompactTagName()).isEqualTo("fourspottedchaser");
        assertThat(tag.getTagName()).isEqualTo("Four-spotted chaser");
    }

    @Test
    public void fetchTag_unknown() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Tag.fetchTag("macwomble"));
        // assertThat(thrown.getMessage(), equalTo("No tag has compact name
        // 'macwomble'"));
        assertThat(thrown).hasMessage("No tag has compact name 'macwomble'");
    }

    @Test
    public void fetchTagIfPresent_unknown() {
        Tag tag = Tag.fetchTagIfPresent("macwomble");

        // assertThat(tag, nullValue());
        assertThat(tag).isNull();
    }

    @Test
    public void compactName() {
        Tag tag = Tag.fetchOrCreateTag("Aa-bb cc");
        assertThat(tag.getCompactTagName()).isEqualTo("aabbcc");

        Tag tag2 = Tag.fetchTagIfPresent("aabbcc");
        assertThat(tag2).isSameAs(tag);
    }
}
