package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.Tiddler;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Implements the <code>FileObjectVisitor</code> interface for a
 * folder/directory represented by a given
 * <code>org.apache.commons.vfs2.FileObject</code>.</p>
 * <p>
 * It creates a <code>Tiddler</code> based on the current directory/folder.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see Tiddler
 */
final class DirectoryVisitor implements FileObjectVisitor {

    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryVisitor.class);

    /**
     * A reference to an existing folder in the file system used as starting
     * point to search for content, like "/Documents/Reference".
     */
    private FileObject rootFolder = null;

    /**
     * Creates a new DirectoryVisitor with default values.
     */
    public DirectoryVisitor() {
        this.rootFolder = null;
    }

    /**
     * Creates a new DirectoryVisitor with a rootFolder.
     *
     * @param rootFolder the reference to an existing folder in the file system
     * used as starting point to search for content, like
     * "/Documents/Reference".
     */
    public DirectoryVisitor(final FileObject rootFolder) {
        this.rootFolder = rootFolder;
    }

    @Override
    public Object visit(FileObject file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException("Parameter file (a directory) is null!");
        }
        if (!(file.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(file);
        }

        LOGGER.debug("Visit file {}.", file.getName().getPath());
        Tiddler tiddler = createTiddler(file);

        return tiddler;
    }

    /**
     * Creates a new Tiddler for a directory.
     *
     * @param file the reference to the directory
     * @param properties the properties of the directory
     * @return the new Tiddler
     * @throws FileSystemException if operation fails
     * @throws IOException if operation fails
     */
    private Tiddler createTiddler(FileObject file)
            throws FileSystemException, IOException {
        LOGGER.debug("Create tiddler for directory {}...", file.getName().getPath());
        Tiddler tiddler = new Tiddler();
        tiddler.setTitle(file.getName().getBaseName());
        tiddler.addTags(filterKeywords(file));
        tiddler.setPath(file.getName().getPath());

        LOGGER.debug("Done create tiddler for directory {}...", file.getName().getPath());
        LOGGER.trace(tiddler.toString());

        return tiddler;
    }

    /**
     * Filters the keywords of a given document.
     *
     * @param file the reference to the file
     * @return a list with filtered keywords
     * @throws FileSystemException if operation failed
     */
    private List<String> filterKeywords(final FileObject file)
            throws FileSystemException {
        List<String> keywords = new ArrayList();

        FileObject parent = file.getParent();
        if (parent != null) {
            String absolutePath = parent.getName().getPath();
            String[] pathNames = absolutePath.split(String.valueOf(File.separatorChar));
            keywords.addAll(Arrays.asList(pathNames));
        }
        return keywords;
    }

}
