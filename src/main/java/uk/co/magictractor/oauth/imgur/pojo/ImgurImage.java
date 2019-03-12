package uk.co.magictractor.oauth.imgur.pojo;

import java.time.LocalDateTime;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.common.TagSet;

public class ImgurImage implements Photo {

	private String id;
	// Imgur uses this identifier (rather than id) when deleting images.
	private String deleteHash;
	private String title;
	private String description;
	private String tags;
	private int width;
	private int height;

	// TODO! use "name", which is the original file name, but with extension stripped
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
		return description;
	}

	@Override
	public TagSet getTagSet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDateTime getDateTimeTaken() {
		// TODO Auto-generated method stub
		return null;
	}

}
