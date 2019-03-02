package uk.co.magictractor.oauth.flickr.pojo;

import java.time.Instant;
import java.time.LocalDateTime;

import uk.co.magictractor.oauth.common.TagSet;

//  "page": 1, "pages": 17, "perpage": 10, "total": "165"

public class FlickrPhoto {

	public String id;
	public String owner;
	public String secret;
	public String server;
	public String farm;
	public String title;
	// obj
	// public String description;
	// TODO! make these lists
	// public List<String> tags;
	// public String tags;
	public TagSet tags;
	// these are not Flickr's auto tags, they are tags using "x:y=z" syntax
	public TagSet machineTagSet;
	public boolean isPublic;
	public boolean isFriend;
	public boolean isFamily;
//	public Boolean ispublic;
//	public boolean isfriend;
//	public boolean isfamily;
	// ...

	// Flickr does not record the time zone for dateTaken
	public LocalDateTime dateTaken;
	// public Instant dateTaken;
	// dateupload=1534007093, datetaken=2018-06-23 13:52:33,
	// datetakengranularity=0.0, datetakenunknown=0.0
	public Instant dateUpload;
}

// {id=29807629188, owner=165576905@N05, secret=f42111017b, server=942, farm=1, title=Tiger hoverfly, ispublic=1, isfriend=0, isfamily=0, dateupload=1532719975, datetaken=2018-07-26 14:21:49, datetakengranularity=0, datetakenunknown=0}
