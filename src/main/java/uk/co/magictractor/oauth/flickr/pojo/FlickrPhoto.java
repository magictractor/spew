package uk.co.magictractor.oauth.flickr.pojo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

//  "page": 1, "pages": 17, "perpage": 10, "total": "165"

public class FlickrPhoto implements Photo {

	private String id;
	private String owner;
	private String secret;
	private String server;
	private String farm;
	private String title;
	// obj
	// public String description;
	// TODO! make these lists
	// public List<String> tags;
	// public String tags;
	private TagSet tags;
	// these are not Flickr's auto tags, they are tags using "x:y=z" syntax
	private TagSet machineTagSet;
	private boolean isPublic;
	private boolean isFriend;
	private boolean isFamily;
//	public Boolean ispublic;
//	public boolean isfriend;
//	public boolean isfamily;
	// ...

	// Flickr does not record the time zone for dateTaken
	private LocalDateTime dateTaken;
	// private Instant dateTaken;
	// public Instant dateTaken;
	// dateupload=1534007093, datetaken=2018-06-23 13:52:33,
	// datetakengranularity=0.0, datetakenunknown=0.0
	private Instant dateUpload;
	// original names a
	private Integer width_o;
	private Integer height_o;

	@Override
	public String getServiceProviderId() {
		return id;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TagSet getTagSet() {
		return tags;
	}

	@Override
	public Instant getDateTimeTaken() {
		// return dateTaken;
		// TODO! can we get the user's Flcikr timezone rather than hardcoding UTC
		return dateTaken.toInstant(ZoneOffset.UTC);
	}

	public Instant getDateTimeUpload() {
		return dateUpload;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public boolean isFamily() {
		return isFamily;
	}

	public boolean isFriend() {
		return isFriend;
	}

	@Override
	public Integer getWidth() {
		return width_o;
	}

	@Override
	public Integer getHeight() {
		return height_o;
	}

}

// {id=29807629188, owner=165576905@N05, secret=f42111017b, server=942, farm=1, title=Tiger hoverfly, ispublic=1, isfriend=0, isfamily=0, dateupload=1532719975, datetaken=2018-07-26 14:21:49, datetakengranularity=0, datetakenunknown=0}
