package uk.co.magictractor.oauth.flickr;

import uk.co.magictractor.oauth.api.OAuthConnection;
import uk.co.magictractor.oauth.api.OAuthRequest;
import uk.co.magictractor.oauth.api.OAuthResponse;
import uk.co.magictractor.oauth.flickr.pojo.Photo;
import uk.co.magictractor.oauth.servers.Flickr;

public class ChangeUploadTime {
	private static final String ALL_EXTRAS = "description,license,date_upload,date_taken,"
			+ "owner_name,icon_server,original_format,last_update,geo,tags,machine_tags,"
			+ "o_dims,views,media,path_alias," + "url_sq,url_t,url_s,url_q,url_m,url_n,url_z,url_c,url_l,url_o";

	// https://api.flickr.com/services/rest/?method=flickr.people.getPhotos
	// &api_key=1da91a5cdef62942f510c37e84ba5f70
	// &user_id=me&format=json&nojsoncallback=1
	// &auth_token=72157696367169942-cc992bdf174d50c7
	// &api_sig=2f89eec3ea014525e7a4cec5d51cf1ab

	private void exec() {
		OAuthRequest request = new OAuthRequest(Flickr.REST_ENDPOINT);

		// https://secure.flickr.com/services/api/flickr.people.getPhotos.html
		// FlickrRequest request = FlickrRequest.forApi("flickr.people.getPhotos");
		request.setParam("method", "flickr.people.getPhotos");

		request.setParam("user_id", "me");
		// machine_tags are no auto tags
		// https://www.flickr.com/groups/51035612836@N01/discuss/72157594497877875/
		request.setParam("extras", "date_upload,date_taken,description,tags,machine_tags");
		// request.setParam("extras", ALL_EXTRAS);
		OAuthResponse response = new OAuthConnection(new Flickr()).request(request);

		// getClass() -> LinkedHashMap in both cases
		System.err.println(response);
		Object photos = response.getObject("photos");
		System.err.println(">" + photos);
		System.err.println(response.getObject("photos").getClass());
		System.err.println(response.getObject("photos.photo[3]"));
		System.err.println(response.getObject("photos.photo[3]").getClass());
		Photo photo = response.getObject("photos.photo[3]", Photo.class);
		System.err.println(">" + photo);
	}

	public static void main(String[] args) {
		ChangeUploadTime change = new ChangeUploadTime();
		change.exec();
	}
}
