package uk.co.magictractor.oauth.local;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class ExifPropertiesSupplierFactoryTest {

	@Test
	public void testDpp4() {
		Photo photo = readPhoto("dpp4.CR2");

		assertThat(photo.getFileName()).isEqualTo("dpp4.CR2");
		assertThat(photo.getRating()).isEqualTo(4);
		// DPP allows ratings to be modified, but not title and description.
		assertThat(photo.getTitle()).isNull();
		assertThat(photo.getDescription()).isNull();
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
