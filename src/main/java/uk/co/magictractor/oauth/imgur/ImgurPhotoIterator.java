package uk.co.magictractor.oauth.imgur;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.api.PageCountServiceIterator;
import uk.co.magictractor.oauth.api.PhotoIterator;
import uk.co.magictractor.oauth.api.connection.OAuthConnectionFactory;
import uk.co.magictractor.oauth.common.filter.PhotoFilter;
import uk.co.magictractor.oauth.imgur.pojo.ImgurImage;
import uk.co.magictractor.oauth.imgur.pojo.ImgurImages;

/*
 * <pre>
 * {
	"data": [{
		"id": "nFp4FhQ",
		"title": null,
		"description": null,
		"datetime": 1551631461,
		"type": "image\/jpeg",
		"animated": false,
		"width": 4522,
		"height": 2543,
		"size": 5021953,
		"views": 0,
		"bandwidth": 0,
		"vote": null,
		"favorite": false,
		"nsfw": null,
		"section": null,
		"account_url": "GazingAtTrees",
		"account_id": 96937645,
		"is_ad": false,
		"in_most_viral": false,
		"has_sound": false,
		"tags": [],
		"ad_type": 0,
		"ad_url": "",
		"edited": "0",
		"in_gallery": false,
		"deletehash": "PtMp42LRT2ruMwR",
		"name": null,
		"link": "https:\/\/i.imgur.com\/nFp4FhQ.jpg"
	}, {
		"id": "tKppqPo",
		"title": null,
		"description": null,
		"datetime": 1548526186,
		"type": "image\/jpeg",
		"animated": false,
		"width": 4046,
		"height": 3037,
		"size": 4814472,
		"views": 9,
		"bandwidth": 43330248,
		"vote": null,
		"favorite": false,
		"nsfw": null,
		"section": null,
		"account_url": "GazingAtTrees",
		"account_id": 96937645,
		"is_ad": false,
		"in_most_viral": false,
		"has_sound": false,
		"tags": [],
		"ad_type": 0,
		"ad_url": "",
		"edited": "0",
		"in_gallery": false,
		"deletehash": "b5YAoRBExapHy3c",
		"name": "PANA2005",
		"link": "https:\/\/i.imgur.com\/tKppqPo.jpg"
	}, {
		"id": "WtFB7Is",
		"title": "Kingfisher",
		"description": null,
		"datetime": 1541407541,
		"type": "image\/jpeg",
		"animated": false,
		"width": 1702,
		"height": 1702,
		"size": 613582,
		"views": 335,
		"bandwidth": 205549970,
		"vote": null,
		"favorite": false,
		"nsfw": null,
		"section": null,
		"account_url": "GazingAtTrees",
		"account_id": 96937645,
		"is_ad": false,
		"in_most_viral": false,
		"has_sound": false,
		"tags": [],
		"ad_type": 0,
		"ad_url": "",
		"edited": "0",
		"in_gallery": false,
		"deletehash": "4wG6JZriZ4gYlzK",
		"name": "IMG_0405",
		"link": "https:\/\/i.imgur.com\/WtFB7Is.jpg"
	}],
	"success": true,
	"status": 200
}
</pre>
 */
/** https://apidocs.imgur.com/#2e45daca-bd44-47f8-84b0-b3f2aa861735 */
//{
//    "data": [{
//        "id": "nFp4FhQ",
//        "title": null,
//        "description": null,
//        "datetime": 1551631461,
//        "type": "image\/jpeg",
//        "animated": false,
//        "width": 4522,
//        "height": 2543,
//        "size": 5021953,
//        "views": 2,
//        "bandwidth": 10043906,
//        "vote": null,
//        "favorite": false,
//        "nsfw": null,
//        "section": null,
//        "account_url": "GazingAtTrees",
//        "account_id": 96937645,
//        "is_ad": false,
//        "in_most_viral": false,
//        "has_sound": false,
//        "tags": [],
//        "ad_type": 0,
//        "ad_url": "",
//        "edited": "0",
//        "in_gallery": false,
//        "deletehash": "PtMp42LRT2ruMwR",
//        "name": null,
//        "link": "https:\/\/i.imgur.com\/nFp4FhQ.jpg"
//    }, {
//        "id": "tKppqPo",
//        "title": null,
//        "description": null,
//        "datetime": 1548526186,
//        "type": "image\/jpeg",
//        "animated": false,
//        "width": 4046,
//        "height": 3037,
//        "size": 4814472,
//        "views": 13,
//        "bandwidth": 62588136,
//        "vote": null,
//        "favorite": false,
//        "nsfw": null,
//        "section": null,
//        "account_url": "GazingAtTrees",
//        "account_id": 96937645,
//        "is_ad": false,
//        "in_most_viral": false,
//        "has_sound": false,
//        "tags": [],
//        "ad_type": 0,
//        "ad_url": "",
//        "edited": "0",
//        "in_gallery": false,
//        "deletehash": "b5YAoRBExapHy3c",
//        "name": "PANA2005",
//        "link": "https:\/\/i.imgur.com\/tKppqPo.jpg"
//    }, {
//        "id": "WtFB7Is",
//        "title": "Kingfisher",
//        "description": null,
//        "datetime": 1541407541,
//        "type": "image\/jpeg",
//        "animated": false,
//        "width": 1702,
//        "height": 1702,
//        "size": 613582,
//        "views": 344,
//        "bandwidth": 211072208,
//        "vote": null,
//        "favorite": false,
//        "nsfw": null,
//        "section": null,
//        "account_url": "GazingAtTrees",
//        "account_id": 96937645,
//        "is_ad": false,
//        "in_most_viral": false,
//        "has_sound": false,
//        "tags": [],
//        "ad_type": 0,
//        "ad_url": "",
//        "edited": "0",
//        "in_gallery": false,
//        "deletehash": "4wG6JZriZ4gYlzK",
//        "name": "IMG_0405",
//        "link": "https:\/\/i.imgur.com\/WtFB7Is.jpg"
//    }],
//    "success": true,
//    "status": 200
//}
public class ImgurPhotoIterator extends PageCountServiceIterator<ImgurImage> implements PhotoIterator<ImgurImage> {

    // min_taken_date (Optional)
    // Minimum taken date. Photos with an taken date greater than or equal to this
    // value will be returned. The date can be in the form of a mysql datetime or
    // unix timestamp.

    public ImgurPhotoIterator(OAuthConnection connection) {
        super(connection);
    }

    @Override
    public Collection<Class<? extends PhotoFilter>> supportedPhotoFilters() {
        // Imgur /images/ does not support filters.
        return Collections.emptySet();
    }

    // Get images https://apidocs.imgur.com/#2e45daca-bd44-47f8-84b0-b3f2aa861735
    @Override
    protected List<ImgurImage> fetchPage(int pageNumber) {
        OAuthRequest request = OAuthRequest
                .createGetRequest(Imgur.REST_ENDPOINT + "/account/me/images/" + (pageNumber - 1));

        // request.setParam("method", "account/me/images/page/" + (pageNumber - 1));

        //		request.setParam("user_id", "me");
        //		request.setParam("sort", "date-taken-desc");
        //		request.setParam("page", pageNumber);
        //		// default is 100, max is 500
        //		request.setParam("per_page", 500);

        OAuthResponse response = getConnection().request(request);

        System.err.println(response);

        ImgurImages images = response.getObject("$", ImgurImages.class);
        //		setTotalItemCount(photos.total);
        //		setTotalPageCount(photos.pages);

        return images.getImages();
    }

    public static void main(String[] args) {
        OAuthConnection connection = OAuthConnectionFactory.getConnection(MyImgurApp.class);
        Iterator<ImgurImage> iter = new ImgurPhotoIterator(connection).builder()
                //.withFilter(new DateTakenPhotoFilter(DateRange.forYear(2019)))
                .build();
        while (iter.hasNext()) {
            ImgurImage photo = iter.next();
            System.err.println(photo.getTitle() + " " + photo.getDateTimeTaken());
        }
    }

}
