package uk.co.magictractor.oauth.local.files;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import uk.co.magictractor.spew.photo.local.files.PathComparators;
import uk.co.magictractor.spew.photo.local.files.PathIterator;

public class PathIteratorTest {

    private static Path testResourcesDirectory;

    private PathIterator pathIterator = new PathIterator(testResourcesDirectory);

    @BeforeAll
    public static void setUpTestResources() throws URISyntaxException {
        URL dirUrl = PathIteratorTest.class.getResource("iterator");
        testResourcesDirectory = Paths.get(dirUrl.toURI());

        assertThat(Files.exists(testResourcesDirectory)).isTrue();
        assertThat(Files.isDirectory(testResourcesDirectory)).isTrue();
    }

    @Test
    public void testDefault() {
        /*
         * Allow any order here - there's no sorting applied, so ordering may
         * vary. On Windows 10, alphabetical sorting appears to be the default.
         */
        assertThat(readActualFileNames()).containsExactlyInAnyOrder("a1.txt", "a2.csv", "a3", "b1a.txt", "b2a.txt",
            "b21a.txt", "d111a.txt");
    }

    @Test
    public void testWithSortingAsc() {
        // Sorting applies to regular files and subdirectory names.
        pathIterator.setOrder(PathComparators.FILENAME_ASC);
        assertThat(readActualFileNames()).containsExactly("a1.txt", "a2.csv", "a3", "b1a.txt", "b21a.txt", "b2a.txt",
            "d111a.txt");
    }

    @Test
    public void testWithSortingDesc() {
        // Sorting applies to regular files and subdirectory names.
        pathIterator.setOrder(PathComparators.FILENAME_DESC);
        assertThat(readActualFileNames()).containsExactly("d111a.txt", "b2a.txt", "b21a.txt", "b1a.txt", "a3", "a2.csv",
            "a1.txt");
    }

    @Test
    public void testWithFileNameFilter() {
        // Note that this is not appied to directory names.
        pathIterator.setFileFilter((p) -> p.getFileName().toString().contains("2"));
        assertThat(readActualFileNames()).containsExactlyInAnyOrder("a2.csv", "b2a.txt", "b21a.txt");
    }

    @Test
    public void testWithDirectoryNameFilter() {
        pathIterator.setDirectoryFilter((p) -> p.getFileName().toString().equals("alpha"));
        assertThat(readActualFileNames()).containsExactlyInAnyOrder("a1.txt", "a2.csv", "a3");
    }

    @Test
    public void testWithDirectoryNameAndFileNameFilter() {
        pathIterator.setDirectoryFilter((p) -> p.getFileName().toString().equals("alpha"));
        pathIterator.setFileFilter((p) -> p.getFileName().toString().endsWith(".txt"));
        assertThat(readActualFileNames()).containsExactlyInAnyOrder("a1.txt");
    }

    private List<String> readActualFileNames() {
        List<String> actualFileNames = new ArrayList<>();
        while (pathIterator.hasNext()) {
            // toString() seems ropey - is there a better method for this?
            actualFileNames.add(pathIterator.next().getFileName().toString());

            // Path path = pathIterator.next();
            // actualFileNames.add(path.getFileName().toString());
        }

        return actualFileNames;
    }

}
