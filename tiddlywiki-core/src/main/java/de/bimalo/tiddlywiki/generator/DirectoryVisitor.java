package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    @Override
    public Object visit(FileObject file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException("Null reference was provided as directory.");
        }

        if (!(file.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(file);
        }

        FileObjectProperties properties = getProperties4Directory(file);
        Tiddler tiddler = createTiddler4Directory(file, properties);

        return tiddler;
    }

    /**
     * Creates a new Tiddler based on the given FileObject and
     * FileObjectProperties.
     *
     * @param file the reference to the directory
     * @param properties the properties of the directory
     * @return the new Tiddler
     * @throws FileSystemException if operation fails
     * @throws IOException if operation fails
     */
    private Tiddler createTiddler4Directory(FileObject file, FileObjectProperties properties)
            throws FileSystemException, IOException {
        LOGGER.trace("Create tiddler for directory {}...", file.getName().getPath());
        DirectoryTiddler tiddler = new DirectoryTiddler();
        tiddler.setName(file.getName().getBaseName());
        tiddler.setTitle(properties.getTitle());
        tiddler.setCreateDate(properties.getLastModifyDate());
        tiddler.setText(properties.getText());
        tiddler.addTags(properties.getKeywords());

        LOGGER.trace("Done create tiddler for directory {}...", file.getName().getPath());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(tiddler.toString());
        }

        return tiddler;
    }

    /**
     * Gets all properties for the given FileObject representing a directory.
     *
     * @param file the reference to a directory
     * @return the FileObjectProperties for the given FileObject.
     * @throws IOException if operation failed
     */
    private FileObjectProperties getProperties4Directory(FileObject file) throws IOException {
        LOGGER.trace("Reading all properties for directory {}...", file.getName().getPath());
        DirectoryProperties properties = new DirectoryProperties(file);
        properties.reload();
        LOGGER.trace("Done reading all properties for directory {}...", file.getName().getPath());
        return properties;
    }
}
