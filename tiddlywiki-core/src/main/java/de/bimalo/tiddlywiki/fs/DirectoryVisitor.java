package de.bimalo.tiddlywiki.fs;

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
      throw new FileNotFoundException("Parameter file (a directory) is null!");
    }
    if (!(file.getType().equals(FileType.FOLDER))) {
      throw new FileNotFolderException(file);
    }
    
    LOGGER.debug("Visit file {}.", file.getName().getPath());
    FileObjectProperties properties = getProperties(file);
    Tiddler tiddler = createTiddler(file, properties);
    
    return tiddler;
  }

  /**
   * Gets the properties for a directory.
   *
   * @param file the reference to a directory
   * @return the FileObjectProperties for the given FileObject.
   * @throws IOException if operation failed
   */
  private FileObjectProperties getProperties(FileObject file) throws IOException {
    LOGGER.debug("Reading properties for directory {}...", file.getName().getPath());
    DirectoryProperties properties = new DirectoryProperties(file);
    properties.reload();
    LOGGER.debug("Done reading properties for directory {}...", file.getName().getPath());
    return properties;
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
  private Tiddler createTiddler(FileObject file, FileObjectProperties properties)
    throws FileSystemException, IOException {
    LOGGER.debug("Create tiddler for directory {}...", file.getName().getPath());
    Tiddler tiddler = new Tiddler();
    tiddler.setTitle(properties.getTitle());
    tiddler.setCreateDate(properties.getLastModifyDate());
    tiddler.setText(properties.getText());
    tiddler.addTags(properties.getKeywords());
    tiddler.setPath(file.getName().getPath());
    
    LOGGER.debug("Done create tiddler for directory {}...", file.getName().getPath());
    LOGGER.trace(tiddler.toString());
    
    return tiddler;
  }
  
}
