package uk.co.magictractor.spew.flickr;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.core.response.parser.SpewParsedResponse;
import uk.co.magictractor.spew.flickr.pojo.FlickrPhoto;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.util.ResourceUtil;

public class FlickrResponseTest {

    @Test
    void t() {
        SpewParsedResponse response = ResourceUtil.readResponse(new MyFlickrApp(), getClass(), "getPhotos.json");

        System.err.println(response);
        Object photos = response.getObject("photos");
        System.err.println(">" + photos);
        System.err.println(response.getObject("photos").getClass());
        System.err.println(response.getObject("photos.photo[0]"));
        System.err.println(response.getObject("photos.photo[0]").getClass());
        FlickrPhoto photo = response.getObject("photos.photo[0]", FlickrPhoto.class);
        System.err.println(">" + photo);

        //		assertThat(photo.id, equalTo("29043424057"));
        //		assertThat(photo.tags, equalTo("kingfisher rbge"));
        //		assertThat(photo.isPublic, equalTo(true));
        //		assertThat(photo.isFriend, equalTo(false));
        //		assertThat(photo.isFamily, equalTo(false));
        //		assertThat(photo.dateUpload, equalTo(Instant.ofEpochMilli(1534007093L)));

        assertThat(photo.getServiceProviderId()).isEqualTo("29043424057");
        // assertThat(photo.tags).isNotNull();
        assertThat(photo.getTagSet()).isEqualTo(new TagSet("kingfisher rbge"));
        assertThat(photo.isPublic()).isTrue();
        assertThat(photo.isFriend()).isFalse();
        assertThat(photo.isFamily()).isFalse();
        // assertThat(photo.dateTaken).isEqualTo(Instant.ofEpochMilli(1534007093L));
        // assertThat(photo.dateTaken).isNotNull();
        // "datetaken": "2018-06-23 13:52:33",
        // "datetaken": "2018-08-09 16:08:38",
        // assertThat(photo.dateTaken).isEqualTo(LocalDateTime.of(2018, 8, 9, 16, 8,
        // 38));
        assertThat(photo.getDateTimeTaken())
                .isEqualTo(ZonedDateTime.of(2018, 6, 23, 13, 52, 33, 0, ZoneOffset.UTC).toInstant());
        assertThat(photo.getDateTimeUpload()).isEqualTo(Instant.ofEpochMilli(1534007093000L));
    }

}
