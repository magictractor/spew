package uk.co.magictractor.oauth.flickr.pojo;

import java.util.List;

public class Photos {

	 // "page": 1, "pages": 17, "perpage": 10, "total": "165"
	
	public int page;
	public int pages;
	public int perPage;
	public int total;
	// TODO! better if this field was called "photos"
	public List<Photo> photo;
}
