package uk.co.magictractor.oauth.google;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.api.OAuthJsonResponse;
import uk.co.magictractor.oauth.util.ResourceUtil;

public class GoogleMediaItemsResponseTest {

	@Test
	void t() {
		String json = ResourceUtil.readResource(this.getClass(), "mediaItems.json");
		OAuthJsonResponse response = new OAuthJsonResponse(json, Google.getInstance().getJsonConfiguration());

		System.err.println(response);
		Object photos = response.getObject("mediaItems");
		System.err.println(">" + photos);
		System.err.println(response.getObject("mediaItems").getClass());
		System.err.println(response.getObject("mediaItems[0]"));
		System.err.println(response.getObject("mediaItems[0]").getClass());
		GoogleMediaItem photo = response.getObject("mediaItems[0]", GoogleMediaItem.class);
		System.err.println(">" + photo);

		assertThat(photo.getFileName()).isEqualTo("IMG_1966.JPG");
		// 2018-11-23T13:25:59Z
		assertThat(photo.getDateTimeTaken()).isEqualTo(LocalDateTime.of(2018, 11, 23, 13, 25, 59));
		assertThat(photo.getShutterSpeed()).isNull();
		assertThat(photo.getAperture()).isEqualTo("5.6");
		assertThat(photo.getIso()).isEqualTo(1000);
//		assertThat(photo.tags).isEqualTo(new TagSet("kingfisher rbge"));
//		assertThat(photo.isPublic).isTrue();
//		assertThat(photo.isFriend).isFalse();
//		assertThat(photo.isFamily).isFalse();

//		assertThat(photo.dateUpload).isEqualTo(Instant.ofEpochMilli(1534007093L));
	}

//	private OAuthResponse buildResponse(String fileName) {
//		InputStream in = getClass().getResourceAsStream(fileName);
//		String json = IOUtil.readAndClose(in);
//		return new OAuthJsonResponse(json, new Google().getJsonConfiguration());
//	}
}
