package uk.co.magictractor.spew.photo.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.Media;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class LocalPhotoTest {

    @Test
    public void testDpp4() {
        Media photo = readPhoto("dpp4.CR2");

        assertThat(photo.getRating()).isEqualTo(4);
        // DPP allows ratings to be modified, but not title and description.
        assertThat(photo.getTitle()).isNull();
        assertThat(photo.getDescription()).isNull();
    }

    private Media readPhoto(String resourceName) {
        URL resourceUrl = getClass().getResource(resourceName);
        URI resourceUri = ExceptionUtil.call(() -> resourceUrl.toURI());
        return new LocalPhoto(Paths.get(resourceUri));
    }
}
