package de.bimalo.tiddlywiki.generator;

import de.bimalo.common.Assert;
import de.bimalo.common.Localizer;
import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.TiddlyWiki;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.commons.vfs2.FileSystemException;

/**
 * <p>
 * Traverses through the local file system and creates a TiddlyWiki containing
 * Tiddler's for every file and folder.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version 1.0
 * @since 1.7
 * @see Tiddler
 * @see TiddlyWiki
 */
public final class FilesystemTraversal {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(FilesystemTraversal.class);

    /**
     * The generated TiddlyWiki.
     */
    private TiddlyWiki wiki = null;

    /**
     * The "first" Tiddler used to construct the TiddlyWiki.
     */
    private Tiddler rootTiddler = null;

    /**
     * A special Tiddler providing a tag cloud for all available Tiddler's. The
     * TagCloud plug in required so that this Tiddler will function correctly.
     */
    private Tiddler tagCloudTiddler = null;

    /**
     * the reference to an existing folder in the file system used as starting
     * point to search for wiki content, like "/Documents/Reference"
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
    private FileObjectVisitor documentVisitor = new DocumentVisitor();

    /**
     * Visitor implementation for direcotries. Can be moved into a factory in a
     * future release.
     */
    private FileObjectVisitor directoryVisitor = new DirectoryVisitor();

    /**
     * Creates a new <code>FilesystemTreeWalter</code>.
     *
     * @param rootFolder the reference to an existing folder in the file system
     * used as starting point to search for wiki content, like
     * "/Documents/Reference"
     * @throws IOException if object creation failed
     */
    public FilesystemTraversal(final FileObject rootFolder) throws IOException {
        Assert.notNull(rootFolder);
        Assert.isTrue(rootFolder.exists());
        if (!(rootFolder.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(rootFolder.getName().getPath());
        }

        this.rootFolder = rootFolder;
        this.localizer = new Localizer();
        this.localizer.setLocale(Locale.getDefault());
    }

    /**
     * Sets the maximum level when traversing the file system hierarchy.
     *
     * @param maxLevel the maximum level. A negativ value deactivates the max
     * level check.
     */
    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Traverse through the file system and creates a new TiddlyWiki with
     * Tiddler's for all found files and folders. Every call to
     * <code>traverse</code> will create a complete new TiddlyWiki!
     *
     * @return a new TiddlyWiki
     * @throws IOException if traversal and TiddlyWiki creation failed
     */
    public TiddlyWiki traverse() throws IOException {
        rootTiddler = createTiddler(rootFolder);
        tagCloudTiddler = createTagCloudTiddler();

        wiki = createTiddlyWiki(rootTiddler);
        wiki.addDefaultTiddler(tagCloudTiddler);

        traverse(rootFolder, rootTiddler, 0);

        return wiki;
    }

    /**
     * Traverses through the hierarchy of the file system.
     *
     * @param parentFolder the parent folder
     * @param parentTiddler the Tiddler represents the parent folder
     * @param level the current hierarchy level
     * @throws IOException if traversal failed for some reason
     */
    private void traverse(FileObject parentFolder, Tiddler parentTiddler, int level)
            throws IOException {
        if (maxLevel > 0 && level == maxLevel) {
            logger.info("maxLevel {} reached.", maxLevel);
            return;
        }

        List<FileObject> children = Arrays.asList(parentFolder.getChildren());
        Collections.sort(children, new FilenameComparator());

        for (int i = 0; i < children.size(); i++) {
            FileObject child = children.get(i);
            logger.trace("Working on file {}...", child.getName().getPath());
            if (!child.isHidden() && isSupportedFileObject(child)) {
                logger.trace("Create tiddler for file {}...", child.getName().getPath());
                Tiddler tiddler = createTiddler(child);
                parentTiddler.addTiddler(tiddler);
                logger.trace("Done.");

                if (child.getType().equals(FileType.FOLDER)) {
                    if (level == 0) {
                        logger.trace("Adds tiddler {} to main menu.", tiddler.getTitle());
                        wiki.addMainMenuTiddler(tiddler);
                    }

                    level++;
                    traverse(child, tiddler, level);
                    level--;
                }
            }
        }
    }

    /**
     * Creates a <code>Tiddler</code> for the given file.
     *
     * @param file create a Tiddler for this file
     * @return the new Tiddler object, it always creates a new one
     * @throws IOException if operation fails
     */
    private Tiddler createTiddler(final FileObject file) throws IOException {
        FileObjectVisitor visitor = getVisitor(file.getType());
        Tiddler tiddler = (Tiddler) visitor.visit(file);
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
     * Creates a <code>TiddlyWiki</code> based on the given
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
     * Creates a <code>TiddlyWiki</code> with a title and sub-title.
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
     * Checks whether the FileObject is supported or not.
     *
     * @param file the file
     * @return true=supported, false = not supported.
     * @throws FileSystemException if operation fails
     */
    private boolean isSupportedFileObject(FileObject file) throws FileSystemException {
        boolean supported = false;

        if (file.getType().equals(FileType.FOLDER)) {
            supported = true;
        } else if (file.getType().equals(FileType.FILE)) {
            String extension = file.getName().getExtension();
            logger.trace("Extension= {}", extension);
            if (extension != null && extension.matches("(txt|TXT|pdf|PDF|doc|DOC|ppt|PPT|xls|XLS|xmind)")) {
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

        logger.trace("Supported FileObject {}.", supported);
        return supported;
    }

}
