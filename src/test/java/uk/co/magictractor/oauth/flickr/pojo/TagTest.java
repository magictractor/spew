package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.Tag;

public class TagTest {

    @Disabled("fetchTag currently requires the canonical name to be passed, which is fine for Flickr, maybe not others")
    @Test
    public void fetchTag_canonical() {
        Tag tag = Tag.fetchTag("Four-spotted chaser");

        assertThat(tag).isNotNull();
        assertThat(tag.getTagName()).isEqualTo("Four-spotted chaser");
    }

    @Test
    public void fetchTag_non_canonical() {
        Tag tag = Tag.fetchTag("fourspottedchaser");

        assertThat(tag).isNotNull();
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

}
