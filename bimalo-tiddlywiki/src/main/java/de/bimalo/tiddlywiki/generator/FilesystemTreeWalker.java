package de.bimalo.tiddlywiki.generator;

import de.bimalo.common.Assert;
import de.bimalo.common.Localizer;
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
     * A special Tiddler providing a tag cloud based on the content of the
     * TiddlyWiki. The TagCloud plug is required so that this Tiddler works
     * correctly.
     */
    private Tiddler tagCloudTiddler = null;

    /**
     * A reference to an existing folder in the file system used as starting
     * point to search for content, like "/Documents/Reference"
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
    private final FileObjectVisitor documentVisitor = new DocumentVisitor();

    /**
     * Visitor implementation for directories. Can be moved into a factory in a
     * future release.
     */
    private final FileObjectVisitor directoryVisitor = new DirectoryVisitor();

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
        rootTiddler = createTiddler(rootFolder);
        tagCloudTiddler = createTagCloudTiddler();

        wiki = createTiddlyWiki(rootTiddler);
        wiki.addDefaultTiddler(tagCloudTiddler);

        walkFileTree(rootFolder, rootTiddler, 0);

        return wiki;
    }

    /**
     * Walks through the hierarchy of the file system and creates the
     * corresponding Tiddler's.
     *
     * @param parentFolder the parent folder
     * @param parentTiddler the Tiddler represents the parent folder
     * @param level the current hierarchy level
     * @throws IOException if traversal failed for some reason
     */
    private void walkFileTree(FileObject parentFolder, Tiddler parentTiddler, int level)
            throws IOException {
        if (maxLevel < 0 || level < maxLevel) {
            List<FileObject> children = listAndSortChildrens(parentFolder);

            for (FileObject child : children) {
                LOGGER.trace("Analyze file {}...", child.getName().getPath());
                if (!child.isHidden() && isSupportedFileType(child)) {
                    Tiddler tiddler = createTiddler(child);
                    parentTiddler.addTiddler(tiddler);

                    if (isDirectory(child)) {
                        if (level == 0) {
                            LOGGER.trace("Adds tiddler {} to main menu.", tiddler.getTitle());
                            wiki.addMainMenuTiddler(tiddler);
                        }

                        level++;
                        walkFileTree(child, tiddler, level);
                        level--;
                    }
                }
            }
        } else {
            LOGGER.info("maxLevel {} reached.", maxLevel);
        }
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
     * Lookup and sorts all documents and directories directly belonging to the
     * parentFolder.
     *
     * @param parentFolder the parent folder
     * @throws FileSystemException if operation failed
     */
    private List<FileObject> listAndSortChildrens(FileObject parentFolder) throws FileSystemException {
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
     * Creates a special Tiddler implementing a tag cloud for all available
     * Tiddler's in the TiddlyWiki file. Needs the TagCloud plugin.
     *
     * @return Tiddler providing a tag cloud
     */
    private Tiddler createTagCloudTiddler() {
        Tiddler tiddler = new Tiddler("TagCloud");
        tiddler.setText("&lt;&lt;cloud limit:10 systemConfig excludeMissing script&gt;&gt;");
        return tiddler;
    }

    /**
     * Creates a new <code>TiddlyWiki</code> based on the given
     * <code>Tiddler</code>.
     *
     * @param rootTiddler the first (root) Tiddler for the TiddlyWiki
     * @return a new TiddlyWiki, it always creates a new one
     */
    private TiddlyWiki createTiddlyWiki(Tiddler rootTiddler) {
        TiddlyWiki wiki = createTiddlyWiki(rootTiddler.getTitle(),
                localizer.formatDateObject(new Date(), "dd.MM.yyyy hh:mm:ss"));
        wiki.addTiddler(rootTiddler);
        wiki.addDefaultTiddler(rootTiddler);
        return wiki;
    }

    /**
     * Creates a new <code>TiddlyWiki</code> with a title and sub-title.
     *
     * @param title the title for the new TiddlyWiki
     * @param subtitle the sub-title for the new TiddlyWiki
     * @return a new TiddlyWiki, it always creates a new one
     */
    private TiddlyWiki createTiddlyWiki(String title, String subtitle) {
        TiddlyWiki wiki = new TiddlyWiki();
        wiki.setTitle(title);
        wiki.setSubtitle(subtitle);
        return wiki;
    }

    /**
     * Checks whether the type of the given FileObject is supported or not.
     *
     * @param file the FileObject
     * @return true=type is supported, otherwise false
     * @throws FileSystemException if operation fails
     */
    private boolean isSupportedFileType(FileObject file) throws FileSystemException {
        boolean supported = false;

        if (file.getType().equals(FileType.FOLDER)) {
            supported = true;
        } else if (file.getType().equals(FileType.FILE)) {
            String extension = file.getName().getExtension();
            LOGGER.trace("Extension= {}", extension);
            if (extension != null && extension.matches("(txt|TXT|pdf|PDF|doc|DOC|docx"
                    + "|ppt|PPT|pptx|xls|XLS|xlsx|itmz|xmind)")) {
                supported = true;
            } else {
                supported = false;
            }
            /**
             * To avoid naming conflicts in the TiddlyWiki it is necessary, that
             * the text file containing the meta data for folders will not be
             * included in the TiddlyWiki. Due to this filter also these files.
             */
            String parentBaseName = file.getName().getParent().getBaseName();
            String baseName = file.getName().getBaseName();
            if (baseName.startsWith(parentBaseName)) {
                supported = false;
            } else {
                supported = true;
            }
        }

        LOGGER.trace("Supported FileObject {}.", supported);
        return supported;
    }

}
