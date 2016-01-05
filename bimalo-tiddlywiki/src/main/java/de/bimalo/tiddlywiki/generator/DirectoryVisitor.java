package de.bimalo.tiddlywiki.generator;

import de.bimalo.common.Assert;
import java.io.IOException;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
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
 * @version 1.0
 * @since 1.7
 * @see FileObjectTiddler
 */
final class DirectoryVisitor implements FileObjectVisitor {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(DirectoryVisitor.class);

    @Override
    public Object visit(FileObject file) throws IOException {
        Assert.notNull(file);
        if (!(file.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(file);
        }

        logger.trace("Gets FileObjectProperties for directory {}...", file.getName().getPath());
        FileObjectProperties properties = getFileObjectProperties(file);
        logger.trace("Done.");

        logger.trace("Create tiddler for directory {}...", file.getName().getPath());
        DirectoryTiddler tiddler = new DirectoryTiddler();
        tiddler.setName(file.getName().getBaseName());
        tiddler.setTitle(properties.getTitle());
        tiddler.setCreateDate(properties.getLastModifyDate());
        tiddler.setText(properties.getText());
        tiddler.addTags(properties.getKeywords());

        if (logger.isTraceEnabled()) {
            logger.trace(tiddler.toString());
        }

        logger.trace("Done.");

        return tiddler;
    }

    /**
     * Gets the FileObjectProperties for the given FileObject.
     *
     * @param file the FileObject
     * @return the FileObjectProperties for the given FileObject.
     * @throws IOException if operation failed
     */
    private FileObjectProperties getFileObjectProperties(FileObject file) throws IOException {
        DirectoryProperties properties = new DirectoryProperties(file);
        properties.reload();
        return properties;
    }
}
