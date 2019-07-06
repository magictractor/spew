package uk.co.magictractor.oauth.flickr.pojo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.common.Tag;

public class TagTest {

    @Test
    public void fetchTag_canonical() {
        Tag tag = Tag.fetchTag("four-spotted chaser");

        assertThat(tag.getTagName()).isEqualTo("four-spotted chaser");
    }

    @Test
    public void fetchTag_nonCanonical() {
        Tag tag = Tag.fetchTag("fourspottedchaser");

        assertThat(tag.getTagName()).isEqualTo("four-spotted chaser");
    }

    @Test
    public void fetchTag_hasAlias() {
        Tag tag = Tag.fetchTag("wool carder bee");

        assertThat(tag.getAliases()).containsExactly("Anthidium manicatum");
    }

    @Test
    public void fetchTag_unknown() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> Tag.fetchTag("macwomble"));
        // assertThat(thrown.getMessage(), equalTo("No tag has compact name
        // 'macwomble'"));
        assertThat(thrown).hasMessage("No tag has compact name 'macwomble'");
    }

    @Test
    public void fetchTagIfPresentCanonical_unknown() {
        Tag tag = Tag.fetchTagIfPresentCanonical("macwomble");

        // assertThat(tag, nullValue());
        assertThat(tag).isNull();
    }

}
