package uk.co.magictractor.spew.photo.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.local.ExifPropertiesSupplierFactory;
import uk.co.magictractor.spew.photo.local.PropertySuppliedPhoto;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class ExifPropertiesSupplierFactoryTest {

    @Test
    public void testDpp4() {
        Photo photo = readPhoto("dpp4.CR2");

        assertThat(photo.getFileName()).isEqualTo("dpp4.CR2");
        assertThat(photo.getRating()).isEqualTo(4);
        // DPP allows ratings to be modified, but not title and description.
        assertThat(photo.getTitle()).isNull();
        assertThat(photo.getDescription()).isNull();
        // TODO! better assert method??
        assertThat(photo.getDateTimeTaken())
                .isEqualTo(ZonedDateTime.of(2018, 7, 26, 14, 42, 25, 0, ZoneOffset.UTC).toInstant());
        assertThat(photo.getShutterSpeed()).isEqualTo("1/200");
        assertThat(photo.getAperture()).isEqualTo("5.6");
        assertThat(photo.getIso()).isEqualTo(125);
        assertThat(photo.getWidth()).isEqualTo(4608);
        assertThat(photo.getHeight()).isEqualTo(3456);
    }

    private Photo readPhoto(String resourceName) {
        URL resourceUrl = getClass().getResource(resourceName);
        URI resourceUri = ExceptionUtil.call(() -> resourceUrl.toURI());
        return PropertySuppliedPhoto.forFactory(new ExifPropertiesSupplierFactory(Paths.get(resourceUri)));
    }
}
