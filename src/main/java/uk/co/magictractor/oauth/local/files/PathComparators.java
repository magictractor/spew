package uk.co.magictractor.oauth.local.files;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class PathComparators {

	public static final Comparator<Path> FILENAME_ASC = Comparator.comparing(Path::getFileName);
	public static final Comparator<Path> FILENAME_DESC = FILENAME_ASC.reversed();

	public static final Comparator<Path> DIRECTORIES_FIRST = Comparator.comparing((path) -> Files.isDirectory(path));
	public static final Comparator<Path> DIRECTORIES_LAST = DIRECTORIES_FIRST.reversed();

	private PathComparators() {
	}

}
