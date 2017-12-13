package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.common.Assert;
import de.bimalo.tiddlywiki.common.Localizer;
import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.TiddlyWiki;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Walks through the file system and creates a TiddlyWiki containing Tiddler's
 * for every document and directory.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see de.bimalo.tiddlywiki.Tiddler
 * @see de.bimalo.tiddlywiki.TiddlyWiki
 */
final class FilesystemTreeWalker {

    /**
     * A list of extensions of files supported by this generator.
     */
    public static final String SUPPORTED_FILE_TYPES = "md|rst|MD|RST|txt|TXT|pdf|"
            + "PDF|doc|DOC|docx|ppt|PPT|pptx|xls|XLS|xlsx|itmz|xmind|jpg|png|PNG|JPG";
    /**
     * Logger instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemTreeWalker.class);

    /**
     * The generated TiddlyWiki.
     */
    private TiddlyWiki wiki = null;

    /**
     * The "first" Tiddler of the TiddlyWiki.
     */
    private Tiddler rootTiddler = null;

    /**
     * A reference to an existing folder in the file system used as starting
     * point to search for content, like "/Documents/Reference".
     */
    private FileObject rootFolder = null;

    /**
     * A Localizer provides localization support.
     */
    private Localizer localizer = null;

    /**
     * The maximum traversal level in the file system hierarchy. A negative
     * value means endless. Default is endless.
     */
    private int maxLevel = -1;

    /**
     * Visitor implementation for documents. This variable is used as cache. Can
     * be moved into a factory in a future release.
     */
    private FileObjectVisitor documentVisitor = null;

    /**
     * Visitor implementation for directories. Can be moved into a factory in a
     * future release.
     */
    private FileObjectVisitor directoryVisitor = null;

    /**
     * Creates a new <code>FilesystemTreeWalker</code>.
     *
     * @param rootFolder the reference to an existing folder in the file system
     * used as starting point to search for content, like
     * "/Documents/Reference".
     * @throws FileNotFolderException if rootFolder is not a directory
     * @throws FileSystemException if existences of rootFolder could not be
     * verified
     * @throws IllegalArgumentException if rootFolder is null or does not exist
     */
    FilesystemTreeWalker(final FileObject rootFolder) throws FileNotFolderException,
            FileSystemException {
        Assert.notNull(rootFolder);
        Assert.isTrue(rootFolder.exists());
        if (!(rootFolder.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(rootFolder.getName().getPath());
        }

        this.rootFolder = rootFolder;
        this.localizer = new Localizer(Locale.getDefault());
        documentVisitor = new DocumentVisitor(this.rootFolder);
        directoryVisitor = new DirectoryVisitor(this.rootFolder);
    }

    /**
     * Sets the maximum level when walking through the file system hierarchy.
     *
     * @param maxLevel the maximum level. A negative value deactivates the max
     * level check.
     */
    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Walks through the file system and creates a new TiddlyWiki with Tiddler's
     * for every document and directory. Every call to <code>walkFileTree</code>
     * will create a complete new TiddlyWiki!
     *
     * @return a new TiddlyWiki
     * @throws IOException if operation failed
     */
    public TiddlyWiki walkFileTree() throws IOException {
        LOGGER.debug("walkFileTree starting with {}...", rootFolder.getName().getPath());
        rootTiddler = createTiddler(rootFolder);
        wiki = createTiddlyWiki(rootTiddler);

        walkFileTree(rootFolder, wiki, 0);

        LOGGER.debug("Done walkFileTree.");
        return wiki;
    }

    /**
     * Walks through the hierarchy of the file system and creates the
     * corresponding Tiddler's.
     *
     * @param parentFolder the parent folder
     * @param TiddlyWiki the generated TiddlyWiki
     * @param level the current hierarchy level
     * @throws IOException if traversal failed for some reason
     */
    private void walkFileTree(FileObject parentFolder, TiddlyWiki wiki, int level)
            throws IOException {
        LOGGER.info("walkFileTree {}...", parentFolder.getName().getPath());

        if (maxLevel < 0 || level < maxLevel) {
            List<FileObject> children = listAndSortChildrens(parentFolder);

            for (FileObject child : children) {
                LOGGER.trace("Analyze file {}...", child.getName().getPath());
                if (!child.isHidden()) {
                    if (isFile(child)) {
                        Tiddler tiddler = createTiddler(child);
                        wiki.addTiddler(tiddler);
                        if (level == 0 || tiddler.isDefault()) {
                            wiki.addDefaultTiddler(tiddler);
                        }
                    } else if (isDirectory(child)) {
                        level++;
                        walkFileTree(child, wiki, level);
                        level--;
                    }
                }
            }
        } else {
            LOGGER.info("maxLevel {} reached.", maxLevel);
        }
        LOGGER.debug("Done walkFileTree for {}.", parentFolder.getName().getPath());
    }

    /**
     * Checks if the given FileObject is a directory.
     *
     * @param file the FileObject to check
     * @return true=it is a directory otherwise false
     * @throws FileSystemException if operation fails
     */
    private boolean isDirectory(FileObject file) throws FileSystemException {
        return file.getType().equals(FileType.FOLDER);
    }

    /**
     * Checks if the given FileObject is a file.
     *
     * @param file the FileObject to check
     * @return true=it is a file otherwise false
     * @throws FileSystemException if operation fails
     */
    private boolean isFile(FileObject file) throws FileSystemException {
        String extension = file.getName().getExtension();
        return file.getType().equals(FileType.FILE) && extension != null
                && extension.matches(SUPPORTED_FILE_TYPES);
    }

    /**
     * Lookup and sorts all documents and directories directly belonging to the
     * parentFolder.
     *
     * @param parentFolder the parent folder
     * @throws FileSystemException if operation failed
     */
    private List<FileObject> listAndSortChildrens(FileObject parentFolder)
            throws FileSystemException {
        List<FileObject> children = Arrays.asList(parentFolder.getChildren());
        Collections.sort(children, new FilenameComparator());
        return children;
    }

    /**
     * Creates a new <code>Tiddler</code> for the given FileObject.
     *
     * @param file the document or directory
     * @return the new Tiddler
     * @throws IOException if operation fails
     */
    private Tiddler createTiddler(final FileObject file) throws IOException {
        LOGGER.trace("Create tiddler for file {}...", file.getName().getPath());
        FileObjectVisitor visitor = getVisitor(file.getType());
        Tiddler tiddler = (Tiddler) visitor.visit(file);
        LOGGER.trace("Tiddler for file {} created", file.getName().getPath());
        return tiddler;
    }

    /**
     * Returns the FileObjectVisitor for the given FileType.
     *
     * @param type the FileType
     * @return FileObjectVisitor for the given FileType
     */
    private FileObjectVisitor getVisitor(FileType type) {
        FileObjectVisitor visitor = null;

        if (type.equals(FileType.FILE)) {
            visitor = documentVisitor;
        } else {
            visitor = directoryVisitor;
        }
        return visitor;
    }

    /**
     * Creates a new <code>TiddlyWiki</code> based on the given
     * <code>Tiddler</code>.
     *
     * @param rootTiddler the first (root) Tiddler for the TiddlyWiki
     * @return a new TiddlyWiki, it always creates a new one
     */
    private TiddlyWiki createTiddlyWiki(Tiddler rootTiddler) {
        TiddlyWiki tmpwiki = createTiddlyWiki(rootTiddler.getTitle(),
                localizer.formatDateObject(new Date(), "dd.MM.yyyy hh:mm:ss"));
        tmpwiki.addTiddler(rootTiddler);
        tmpwiki.addDefaultTiddler(rootTiddler);
        return tmpwiki;
    }

    /**
     * Creates a new <code>TiddlyWiki</code> with a title and sub-title.
     *
     * @param title the title for the new TiddlyWiki
     * @param subtitle the sub-title for the new TiddlyWiki
     * @return a new TiddlyWiki, it always creates a new one
     */
    private TiddlyWiki createTiddlyWiki(String title, String subtitle) {
        TiddlyWiki tmpwiki;
        tmpwiki = new TiddlyWiki();
        tmpwiki.setTitle(title);
        tmpwiki.setSubtitle(subtitle);
        return tmpwiki;
    }

}
