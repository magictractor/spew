package uk.co.magictractor.oauth.common.filter;

import uk.co.magictractor.oauth.common.Photo;
import uk.co.magictractor.oauth.util.ExceptionUtil;

public class DateTakenPhotoFilter implements PhotoFilter {

	@Override
	public boolean test(Photo t) {
		throw ExceptionUtil.notYetImplemented();
	}

}
