package uk.co.magictractor.oauth.local.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

// See https://stackoverflow.com/questions/3154488/how-do-i-iterate-through-the-files-in-a-directory-in-java
// For Java 7+:
// Files.walkFileTree() - visitor pattern - could change processor chain to work with visitor or iterator
// Files.newDirectoryStream - cannot skip whole directories based on date range [minor: don't have control over ordering]
public class PathIterator extends AbstractIterator<Path> {

    private boolean hasStarted;

    private List<Path> roots = new ArrayList<>();
    private Predicate<Path> fileFilter;
    private Predicate<Path> directoryFilter;
    private Comparator<Path> order;

    //	// Used to order non-subdirectory files in a directory.
    //	private Comparator<File> photoComparator = Comparator.comparing(File::getName);
    //	// Used to order subdirectories.
    //	private Comparator<File> subdirectoryComparator = Comparator.comparing(File::getName);
    //	private Predicate<Path> subdirectoryPredicate;
    //	// Perhaps just use single comparator, then don't need this?
    //	private boolean depthFirst = true;

    //	private Iterator<LocalPhoto> directoryIterator;
    //	private Iterator<LocalPhoto> subdirectoriesIterator;

    // Use stack rather than recursion, similar to FileTreeWalker.
    private final Deque<DirectoryNode> directoryStack = new ArrayDeque<>();

    public PathIterator(Path root) {
        roots.add(root);
    }

    public void addRoot(Path root) {
        checkNotStarted();
        roots.add(root);
    }

    public void setFileFilter(Predicate<Path> fileFilter) {
        checkNotStarted();
        this.fileFilter = fileFilter;
    }

    public void setDirectoryFilter(Predicate<Path> directoryFilter) {
        checkNotStarted();
        this.directoryFilter = directoryFilter;
    }

    private Predicate<Path> filter() {
        if (fileFilter != null && directoryFilter != null) {
            return (path) -> Files.isDirectory(path) ? directoryFilter.test(path) : fileFilter.test(path);
        }
        else if (directoryFilter != null) {
            return (path) -> !Files.isDirectory(path) || directoryFilter.test(path);
        }
        else if (fileFilter != null) {
            return (path) -> Files.isDirectory(path) || fileFilter.test(path);
        }
        else {
            // No filter
            return null;
        }
    }

    public void setOrder(Comparator<Path> order) {
        checkNotStarted();
        this.order = order;
    }

    private void checkNotStarted() {
        if (hasStarted) {
            throw new IllegalStateException(
                getClass().getSimpleName() + " cannot be reconfigured after hasNext() or next() has been called");
        }

    }

    private class DirectoryNode {
        private final Path directory;
        // private final Iterator<String> fileNameIterator;
        private final Iterator<Path> fileIterator;
        // private final DirectoryStream<Path> fileStream;

        DirectoryNode(Path directory) {
            this.directory = directory;
            // TODO! can pass filename filter to list()
            //			String[] files = directory.list();
            //			// TODO! sort - but default should be no sorting for performance
            //			fileNameIterator = Arrays.asList(files).iterator();

            try {
                // fileStream = Files.newDirectoryStream(this.directory, (path) -> true);

                // ah! directory stream is not a proper stream - where to do sort??
                // Files.isRegularFile(path, options)
                // Paths.

                Stream<Path> fileStream = Files.list(directory);
                if (order != null) {
                    fileStream = fileStream.sorted(order);
                }
                Predicate<Path> filter = filter();
                if (filter != null) {
                    fileStream = fileStream.filter(filter);
                }

                fileIterator = fileStream.iterator();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalStateException();
            }
        }

        boolean hasNext() {
            return fileIterator.hasNext();
            // return fileStream.
        }

        Path next() {
            return fileIterator.next();
        }
    }

    //	private class DirectoryNode {
    //		private final File directory;
    //		private final Iterator<String> fileNameIterator;
    //
    //		DirectoryNode(File directory) {
    //			this.directory = directory;
    //			// TODO! can pass filename filter to list()
    //			String[] files = directory.list();
    //			// TODO! sort
    //			fileNameIterator = Arrays.asList(files).iterator();
    //		}
    //
    //		boolean hasNext() {
    //			return fileNameIterator.hasNext();
    //		}
    //
    //		File next() {
    //			return new File(directory, fileNameIterator.next());
    //		}
    //	}

    @Override
    protected Path computeNext() {
        Path result;

        if (!hasStarted) {
            init();
        }

        do {
            if (directoryStack.isEmpty()) {
                // Done.
                return endOfData();
            }
            DirectoryNode directoryNode = directoryStack.peek();
            if (directoryNode.hasNext()) {
                Path next = directoryNode.next();
                if (Files.isDirectory(next)) {
                    /*
                     * Descend into directory. Depending on config, either
                     * returning the directory now, or iterate to find a file
                     * within the directory.
                     */
                    result = push(next);
                }
                else {
                    /*
                     * Return a (non-subdirectory) file from the current
                     * directory.
                     */
                    result = next;
                }
            }
            else {
                /*
                 * No more files in the directory or its subdirectories.
                 * Depending on config, either return the directory now, or
                 * iterate to find more files.
                 */
                result = pop();
            }
        } while (result == null);

        return result;
    }

    private void init() {
        hasStarted = true;
        Lists.reverse(roots).forEach(this::push);
    }

    private Path push(Path directory) {
        directoryStack.push(new DirectoryNode(directory));
        // TODO! if incl-dir && width-first(?) return directory
        return null;
    }

    private Path pop() {
        directoryStack.pop();
        // TODO! if incl-dir && depth-first(?) return directory
        return null;
    }

    //	public void setDateRange(DateRange dateRange, LocalDirectoryDatesStrategy localDirectoryDatesStrategy) {
    //		// Iterators.concat(inputs);
    //
    //		addPhotoPredicate((photo) -> dateRange.contains(photo.getDateTaken()));
    //		// TODO! how to pass in outer range from parent directory
    //		// TODO! strategy may return null
    //		// RESUME HERE
    //		addSubdirectoryPredicate((subdirectory) -> dateRange
    //				.intersects(localDirectoryDatesStrategy.getDateRange(subdirectory.getFileName(), null)));
    //	}
    //
    //	public void addPhotoPredicate(Predicate<LocalPhoto> photoPredicate) {
    //		if (this.photoPredicate == null) {
    //			this.photoPredicate = photoPredicate;
    //		} else {
    //			this.photoPredicate = this.photoPredicate.and(photoPredicate);
    //		}
    //	}
    //
    //	public void addSubdirectoryPredicate(Predicate<LocalDirectory> subdirectoryPredicate) {
    //		if (this.subdirectoryPredicate == null) {
    //			this.subdirectoryPredicate = subdirectoryPredicate;
    //		} else {
    //			this.subdirectoryPredicate = this.subdirectoryPredicate.and(subdirectoryPredicate);
    //		}
    //	}
}
