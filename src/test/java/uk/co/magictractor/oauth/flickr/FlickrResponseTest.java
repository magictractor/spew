package uk.co.magictractor.oauth.flickr;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.oauth.api.OAuthJsonResponse;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.flickr.pojo.TagSet;
import uk.co.magictractor.oauth.util.IOUtil;

public class FlickrResponseTest {

	@Test
	void t() {
		OAuthResponse response = buildResponse("getPhotos.json");

		System.err.println(response);
		Object photos = response.getObject("photos");
		System.err.println(">" + photos);
		System.err.println(response.getObject("photos").getClass());
		System.err.println(response.getObject("photos.photo[0]"));
		System.err.println(response.getObject("photos.photo[0]").getClass());
		Photo photo = response.getObject("photos.photo[0]", Photo.class);
		System.err.println(">" + photo);

//		assertThat(photo.id, equalTo("29043424057"));
//		assertThat(photo.tags, equalTo("kingfisher rbge"));
//		assertThat(photo.isPublic, equalTo(true));
//		assertThat(photo.isFriend, equalTo(false));
//		assertThat(photo.isFamily, equalTo(false));
//		assertThat(photo.dateUpload, equalTo(Instant.ofEpochMilli(1534007093L)));

		assertThat(photo.id).isEqualTo("29043424057");
		// assertThat(photo.tags).isNotNull();
		assertThat(photo.tags).isEqualTo(new TagSet("kingfisher rbge"));
		assertThat(photo.isPublic).isTrue();
		assertThat(photo.isFriend).isFalse();
		assertThat(photo.isFamily).isFalse();
		// assertThat(photo.dateTaken).isEqualTo(Instant.ofEpochMilli(1534007093L));
		// assertThat(photo.dateTaken).isNotNull();
		// "datetaken": "2018-06-23 13:52:33",
		// "datetaken": "2018-08-09 16:08:38",
		// assertThat(photo.dateTaken).isEqualTo(LocalDateTime.of(2018, 8, 9, 16, 8,
		// 38));
		assertThat(photo.dateTaken).isEqualTo(LocalDateTime.of(2018, 6, 23, 13, 52, 33));
		assertThat(photo.dateUpload).isEqualTo(Instant.ofEpochMilli(1534007093L));
	}

	private OAuthResponse buildResponse(String fileName) {
		InputStream in = getClass().getResourceAsStream(fileName);
		String json = IOUtil.readAndClose(in);
		return new OAuthJsonResponse(json, new Flickr().getJsonConfiguration());
	}
}
