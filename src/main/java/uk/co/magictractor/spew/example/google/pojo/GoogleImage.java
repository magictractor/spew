package uk.co.magictractor.spew.example.google.pojo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

import uk.co.magictractor.spew.photo.Image;
import uk.co.magictractor.spew.photo.Photo;
import uk.co.magictractor.spew.photo.TagSet;
import uk.co.magictractor.spew.photo.Video;

//{
//    "id": "AO6co5uaPax0_68QU28KxDL3ZySSl-znViY5GJpRQrS3b1mE__i0xoZBYjalUiXj5GNcR_9WTquqF_L5RpQae-eWxcBIANY7mg",
//    "productUrl": "https://photos.google.com/lr/photo/AO6co5uaPax0_68QU28KxDL3ZySSl-znViY5GJpRQrS3b1mE__i0xoZBYjalUiXj5GNcR_9WTquqF_L5RpQae-eWxcBIANY7mg",
//    "baseUrl": "https://lh3.googleusercontent.com/lr/AGWb-e5y_R2qu8RmUThNIKTNgYMkZA2mhYeAg76rAy9cqX-5FFKiXLI4CCK9leslIxj9B9QZNnH62dkik4zd6RjNivD4_VRK2tEVEvFoMxzf7CQIACe9qXOawYzOZiiM1nOJ50gsGoMzHCcp7ZKNJphBIzR4MfVTqDl2v81A8sO6c163UvQN6pB81F4_qBbwVGzR23bQ_Uhg-3z5ZnRTvPh-O-Xd6693AZ1VsRoC2nRH3JqWdPw-PGnR3sf6cmOVKiyNlpgvDYI0qtGaaBP66qTem6L524OvXqwG8xGNDzTGAd79FE1pvNJQfDeCmP5NG5zf2VHHln8gBOL1pgiP4yFcQgzNqDLsO9HlNiPEzUywcCV8bavQv0n5k1AHCOpCkVEEo5jsq-EM44cQy_QMCqIVeMcHayzZAu8GntT05JD4kSEvsEM0lx8mV31QZcR0fghn_1HstzarUze84XXn1Iuy2Kam_8dgR32q4k1M6bbfN9LMUR24MGYbDJmJYl4yvhvu7BqQZo3Vk_3GTYdyWIBbenoE1IiDNE0HR6vseZN6vFXdfCHKS-kWjL2VNKyYeH7WW1mSReR-bETuAJmvPno-03Ymegn51YXOMyNd_XAQbSaRvMYcQbPRFzmNPvh1gFpdZ5OHNwQcBskPOPUsumxXx0COtjYiOwXZuMK4fZvPx1gZugksA4ADGDyUBBG83cn8SW5PNUwKWoxzpV70rT3N_7RypvA_yFN80c8BpqR_wsD11W5DQqKvWsqfk8-XuY_d27eGspUb2CvmmWQqWhUCqvW9cNtrdsa8simFBbAMauiOBEwgq2gVw19P_S6W1CcOTAnZELtHvp-iUf99hihjgoJuFGtUcEi0UwEVf44ZXKT_Ucx3CFe5UslBe1a9A_p3hCz_legXZL_B",
//    "mimeType": "image/jpeg",
//    "mediaMetadata": {
//      "creationTime": "2018-11-20T15:09:42Z",
//      "width": "3677",
//      "height": "2758",
//      "photo": {
//        "cameraMake": "Canon",
//        "cameraModel": "Canon PowerShot SX60 HS",
//        "focalLength": 188.038,
//        "apertureFNumber": 6.3,
//        "isoEquivalent": 100
//      }
//    },
//    "filename": "IMG_1763.JPG"
//  }
public class GoogleImage implements Image, Supplier<GoogleImage> {

    private String id;
    private String description;
    private String productUrl;
    private String baseUrl;
    private GoogleMediaMetadata mediaMetadata;
    private String filename;
    private String mimeType;

    @Override
    public GoogleImage get() {
        if (mediaMetadata.photo != null) {
            return new GooglePhoto().copyFrom(this);
        }
        else if (mediaMetadata.video != null) {
            return new GoogleVideo().copyFrom(this);
        }
        throw new IllegalStateException(getClass().getSimpleName() + " contains neither photo nor video metadata");
    }

    /* default */ GoogleImage copyFrom(GoogleImage other) {
        id = other.id;
        description = other.description;
        productUrl = other.productUrl;
        baseUrl = other.baseUrl;
        mediaMetadata = other.mediaMetadata;
        filename = other.filename;

        return this;
    }

    public static final class GoogleMediaMetadata {
        // Google does have zone information "2018-11-20T15:09:42Z" => instant here?
        private LocalDateTime creationTime;
        private int width;
        private int height;
        private GooglePhotoMetadata photo;
        private GoogleVideoMetadata video;
    }

    public static final class GooglePhotoMetadata {
        private String cameraMake;
        private String cameraModel;
        private String focalLength;
        private String apertureFNumber;
        private Integer isoEquivalent;
    }

    public static final class GoogleVideoMetadata {
        private String cameraMake;
        private String cameraModel;
        private Integer fps;
    }

    @Override
    public String getServiceProviderId() {
        return id;
    }

    @Override
    public String getFileName() {
        return filename;
    }

    @Override
    public String getTitle() {
        // Google Photos does not have titles, just a file name and a description.
        return null;
    }

    @Override
    public String getDescription() {
        // TODO! ignore tags if they are hacked into the description
        return description;
    }

    @Override
    public Instant getDateTimeTaken() {
        // return mediaMetadata.creationTime.toLocalDateTime();
        return mediaMetadata.creationTime.toInstant(ZoneOffset.UTC);
    }

    @Override
    public Instant getDateTimeUpload() {
        // upload time is not returned by the Google API
        return null;
    }

    @Override
    public TagSet getTagSet() {
        // TODO Google does not support tags
        // probably mimic by adding tags in description "Kingfisher [kingfisher bird
        // rbge
        // edinburgh]"
        return null;
    }

    @Override
    public Integer getWidth() {
        return mediaMetadata.width;
    }

    @Override
    public Integer getHeight() {
        return mediaMetadata.height;
    }

    //// Photo

    public String getShutterSpeed() {
        // Curiously, this isn't returned although aperture, focal length etc are
        return null;
    }

    public String getAperture() {
        return mediaMetadata.photo.apertureFNumber;
    }

    public Integer getIso() {
        return mediaMetadata.photo.isoEquivalent;
    }

    //// Video

    public Integer durationMillis() {
        return null;
    }

    public Integer framesPerSecond() {
        return mediaMetadata.video.fps;
    }

    public static class GooglePhoto extends GoogleImage implements Photo {
    }

    //    "video": {
    //        "cameraMake": "MAKE_OF_THE_CAMERA",
    //        "cameraModel": "MODEL_OF_THE_CAMERA",
    //        "fps": "FRAME_RATE_OF_THE_VIDEO",
    //        "status": "READY"
    //       }
    // https://developers.google.com/photos/library/guides/access-media-items
    public static class GoogleVideo extends GoogleImage implements Video {
    }

}
