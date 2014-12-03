package de.bimalo.common;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * <p>
 * FileList allows to traverse a folder hierarchy beginning with a specified
 * folder and selects only the files matching the specified regular
 * expression.</p>
 * <p>
 * After creation of the FileList the root folder cannot be modified. However
 * when calling listFiles the processing starts again! The result is not cached
 * internally!</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Revision: $ $Date: $
 * @since 1.0
 */
public final class FileList {

    /**
     * The directory used as start traversing the file system.
     */
    private File rootDir;
    /**
     * The pattern used to search for files in a directory.
     */
    private Pattern pattern;

    /**
     * Constructs a new <code>DirectoryList</code>.
     *
     * @param root the directory to start from
     * @param regexp regular expression used to search for files
     * @exception IllegalArgumentException if root or regexp is null
     * @throws FileNotFoundException if root points to a directory that does not
     * exists
     * @throws PatternSyntaxException if the regexp's syntax is invalid
     */
    public FileList(String root, String regexp) throws FileNotFoundException,
            PatternSyntaxException {
        Assert.notNull(root);
        Assert.notNull(regexp);

        rootDir = new File(root);
        if (!rootDir.exists()) {
            throw new FileNotFoundException(rootDir + " does not exist.");
        }
        pattern = Pattern.compile(regexp);
    }

    /**
     * Gets a list of <code>File</code> objects available in the provided
     * directory.
     *
     * @return a list of File objects.
     */
    public List listFiles() {
        List filesList = listFiles(rootDir);
        return filesList;
    }

    /**
     * <p>
     * Loops through the folder hierarchy based on directory and include all
     * files in the result list matching the defined pattern.</p>
     *
     * @param directory the directory to start traversing the hierarchy
     * @return a List with File objects matching the defined pattern
     */
    private List listFiles(File directory) {
        List fileList = new ArrayList();
        if (directory.canRead()) {
            File[] files = directory.listFiles(new FileListFilter(pattern));
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    fileList.addAll(listFiles(file));
                } else {
                    fileList.add(file);
                }
            }
        }
        return fileList;

    }

    /**
     * @see FileFilter
     */
    private class FileListFilter implements FileFilter {

        /**
         * The regular expression to filter Files.
         */
        private Pattern pattern;

        /**
         * Creates a new <code>DirectoryListerFilenameFilter</code>.
         *
         * @param pattern the regular expression to apply as filter
         */
        public FileListFilter(Pattern pattern) {
            this.pattern = pattern;
        }

        /**
         * @see FileFilter#accept(File)
         */
        public boolean accept(File pathname) {
            boolean result = false;

            if (pathname.isDirectory()) {
                result = true;
            } else {
                String name = pathname.getName();
                result = (pattern.matcher(name).matches());
            }
            return result;
        }
    }
}
