package uk.co.magictractor.oauth.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.common.Photo;
import uk.co.magictractor.spew.local.PropertySuppliedPhoto;
import uk.co.magictractor.spew.local.SidecarPropertiesSupplierFactory;
import uk.co.magictractor.spew.util.ExceptionUtil;

public class SidecarPropertiesSupplierFactoryTest {

    @Test
    public void testDigikamSidecar() {
        Photo sidecar = readSidecar("digikam.RW2.xmp");

        assertThat(sidecar.getRating()).isEqualTo(4);
        assertThat(sidecar.getTitle()).isEqualTo("Heron eating frog");
        assertThat(sidecar.getDescription()).isEqualTo("The herons have been catching lots of frogs this week.");
        // TODO! tidier milli to nano conversion
        // TODO! better assert method??
        assertThat(sidecar.getDateTimeTaken())
                .isEqualTo(ZonedDateTime.of(2019, 3, 15, 14, 7, 10, 37 * 1000000, ZoneOffset.UTC).toInstant());
        assertThat(sidecar.getShutterSpeed()).isEqualTo("10/2500"); // TODO! convert to 1/250
        assertThat(sidecar.getAperture()).isEqualTo("63/10"); // // TODO! convert/standardise
        // Digikam does not copy width and height to the sidecar
        assertThat(sidecar.getWidth()).isNull();
        assertThat(sidecar.getHeight()).isNull();
    }

    @Test
    public void testDarktableSidecar() {
        Photo sidecar = readSidecar("darktable.RW2.xmp");

        assertThat(sidecar.getRating()).isEqualTo(2);
        assertThat(sidecar.getTitle()).isEqualTo("title");
        assertThat(sidecar.getDescription()).isEqualTo("description");
        // darktable copies none of these values into the sidecar
        assertThat(sidecar.getDateTimeTaken()).isNull();
        assertThat(sidecar.getShutterSpeed()).isNull();
        assertThat(sidecar.getAperture()).isNull();
        assertThat(sidecar.getIso()).isNull();
        assertThat(sidecar.getWidth()).isNull();
        assertThat(sidecar.getHeight()).isNull();
    }

    @Test
    public void testAdobeBridgeSidecar() {
        Photo sidecar = readSidecar("adobe_bridge.xmp");

        assertThat(sidecar.getRating()).isEqualTo(3);
        assertThat(sidecar.getTitle()).isEqualTo("title");
        assertThat(sidecar.getDescription()).isEqualTo("description");
        // TODO! tidier milli to nano conversion
        // TODO! better assert method??
        assertThat(sidecar.getDateTimeTaken())
                .isEqualTo(ZonedDateTime.of(2019, 3, 15, 14, 7, 10, 37 * 1000000, ZoneOffset.UTC).toInstant());
        // ZonedDateTimeAssert.
        assertThat(sidecar.getShutterSpeed()).isEqualTo("1/250");
        assertThat(sidecar.getAperture()).isEqualTo("63/10"); // TODO! convert/standardise
        assertThat(sidecar.getIso()).isEqualTo(200);
        assertThat(sidecar.getWidth()).isEqualTo(5184);
        assertThat(sidecar.getHeight()).isEqualTo(3888);
    }

    private Photo readSidecar(String resourceName) {
        URL resourceUrl = getClass().getResource(resourceName);
        URI resourceUri = ExceptionUtil.call(() -> resourceUrl.toURI());
        return PropertySuppliedPhoto.forFactory(new SidecarPropertiesSupplierFactory(Paths.get(resourceUri)));
    }
}
