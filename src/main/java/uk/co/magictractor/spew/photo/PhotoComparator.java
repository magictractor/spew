package uk.co.magictractor.spew.photo;

import java.util.Comparator;

public final class PhotoComparator {

    public static final Comparator<Photo> DATE_TIME_ASC = Comparator.comparing(Photo::getDateTimeTaken);
    public static final Comparator<Photo> DATE_TIME_DESC = DATE_TIME_ASC.reversed();

    private PhotoComparator() {
    }

}