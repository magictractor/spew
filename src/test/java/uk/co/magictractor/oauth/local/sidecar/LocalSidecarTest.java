package uk.co.magictractor.oauth.local.sidecar;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class LocalSidecarTest {

	@Test
	public void testDigikamSidecar() {
		Photo sidecar = readSidecar("digikam.RW2.xmp");

		assertThat(sidecar.getRating()).isEqualTo(4);
		assertThat(sidecar.getTitle()).isEqualTo("Heron eating frog");
		assertThat(sidecar.getDescription()).isEqualTo("The herons have been catching lots of frogs this week.");
	}

	@Test
	public void testDarktableSidecar() {
		Photo sidecar = readSidecar("darktable.RW2.xmp");

		assertThat(sidecar.getRating()).isEqualTo(2);
		assertThat(sidecar.getTitle()).isEqualTo("title");
		assertThat(sidecar.getDescription()).isEqualTo("description");
	}

	@Test
	public void testAdobeBridgeSidecar() {
		Photo sidecar = readSidecar("adobe_bridge.xmp");

		assertThat(sidecar.getRating()).isEqualTo(3);
		assertThat(sidecar.getTitle()).isEqualTo("title");
		assertThat(sidecar.getDescription()).isEqualTo("description");
	}

	private Photo readSidecar(String resourceName) {
		URL resourceUrl = getClass().getResource(resourceName);
		URI resourceUri = ExceptionUtil.call(() -> resourceUrl.toURI());
		return new LocalSidecar(Paths.get(resourceUri));
	}
}
