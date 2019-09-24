package uk.co.magictractor.spew.provider.google;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.example.google.MyGooglePhotosApp;
import uk.co.magictractor.spew.example.google.pojo.GoogleImage;
import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.util.ResourceUtil;

public class GoogleMediaItemsResponseTest {

    @Test
    void t() {
        SpewParsedResponse response = ResourceUtil.readResponse(new MyGooglePhotosApp(), getClass(), "mediaItems.json");

        System.err.println(response);
        Object photos = response.getObject("mediaItems");
        System.err.println(">" + photos);
        System.err.println(response.getObject("mediaItems").getClass());
        System.err.println(response.getObject("mediaItems[0]"));
        System.err.println(response.getObject("mediaItems[0]").getClass());
        Photo photo = (Photo) response.getObject("mediaItems[0]", GoogleImage.class);
        System.err.println(">" + photo);

        assertThat(photo.getFileName()).isEqualTo("IMG_1966.JPG");
        // 2018-11-23T13:25:59Z
        assertThat(photo.getDateTimeTaken())
                .isEqualTo(LocalDateTime.of(2018, 11, 23, 13, 25, 59).toInstant(ZoneOffset.UTC));
        assertThat(photo.getShutterSpeed()).isNull();
        assertThat(photo.getAperture()).isEqualTo("5.6");
        assertThat(photo.getIso()).isEqualTo(1000);
        //		assertThat(photo.tags).isEqualTo(new TagSet("kingfisher rbge"));
        //		assertThat(photo.isPublic).isTrue();
        //		assertThat(photo.isFriend).isFalse();
        //		assertThat(photo.isFamily).isFalse();

        //		assertThat(photo.dateUpload).isEqualTo(Instant.ofEpochMilli(1534007093L));
    }

}
