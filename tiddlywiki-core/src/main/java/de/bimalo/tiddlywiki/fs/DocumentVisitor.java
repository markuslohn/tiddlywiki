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
 * Implements the <code>FileObjectVisitor</code> interface for a file
 * represented by a given <code>org.apache.commons.vfs2.FileObject</code>.</p>
 * <p>
 * It creates a <code>Tiddler</code> based on the current file/document.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see Tiddler
 */
final class DocumentVisitor implements FileObjectVisitor {

  /**
   * Logger instance.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentVisitor.class);

  @Override
  public Object visit(final FileObject file) throws IOException {
    if (file == null) {
      throw new FileNotFoundException("Parameter file (a file) is null!");
    }
    if (!(file.getType().equals(FileType.FILE))) {
      throw new FileNotFolderException(file);
    }

    LOGGER.debug("Visit file {}.", file.getName().getPath());
    FileObjectProperties properties = getProperties(file);
    Tiddler tiddler = createTiddler(file, properties);

    return tiddler;
  }

  /**
   * Gets the properties for the given <code>FileObject</code>.
   *
   * @param file a FileObject representing a file
   * @return the FileObjectProperties for the given FileObject.
   * @throws IOException if operation failed
   */
  private FileObjectProperties getProperties(final FileObject file) throws IOException {
    FileObjectProperties properties = null;

    LOGGER.debug("Read properties for file {}...", file.getName().getPath());
    String contentType = file.getContent().getContentInfo().getContentType();
    LOGGER.trace("Content-Type= {}.", contentType);
    if (contentType == null || contentType.equalsIgnoreCase("text/plain")) {
      properties = new TextDocumentProperties(file);
    } else {
      properties = new BinaryDocumentProperties(file);
    }
    properties.reload();
    LOGGER.debug("Done reading properties for file {}.", file.getName().getPath());

    return properties;
  }

  /**
   * Creates a new Tiddler for a file.
   *
   * @param file the reference to the file
   * @param properties the properties of the file
   * @return the new Tiddler
   * @throws FileSystemException if operation fails
   * @throws IOException if operation fails
   */
  private Tiddler createTiddler(FileObject file, FileObjectProperties properties)
    throws FileSystemException, IOException {
    LOGGER.debug("Create tiddler for file {}...", file.getName().getPath());

    Tiddler tiddler = new Tiddler();
    tiddler.setTitle(properties.getTitle());
    tiddler.setCreator(properties.getAuthor());
    tiddler.setModifier(properties.getAuthor());
    tiddler.setCreateDate(properties.getLastModifyDate());
    tiddler.addTags(filterKeywords(file, properties));
    tiddler.setText(buildText(file, properties));
    tiddler.setPath(file.getName().getPath());
    tiddler.setContentType(properties.getContentType());

    LOGGER.debug("Done create tiddler for file {}...", file.getName().getPath());
    LOGGER.trace(tiddler.toString());

    return tiddler;
  }

  /**
   * Builds the text for a Tiddler.
   *
   * @param file the reference to the file
   * @param properties the properties of the file
   * @return the generated text
   */
  private String buildText(final FileObject file, final FileObjectProperties properties) {
    StringBuilder text = new StringBuilder();

    String description = properties.getDescription();
    if (description != null && !description.isEmpty()) {
      text.append(description);
      text.append("\n\n");
    }

    text.append("[[");
    text.append(file.getName().getBaseName()).append("|").append(file.getName().getURI());
    text.append("]]\n");
    text.append("\n");

    return text.toString();
  }

  /**
   * Filters the keywords of a given document.
   *
   * @param file the reference to the file
   * @param properties the properties of the file
   * @return a list with filtered keywords
   * @throws FileSystemException if operation failed
   */
  private List<String> filterKeywords(final FileObject file, final FileObjectProperties properties)
    throws FileSystemException {
    List<String> keywords = new ArrayList(properties.getKeywords());

    FileObject parent = file.getParent();
    String absolutePath = parent.getName().getPath();
    String[] pathNames = absolutePath.split(File.separator);
    keywords.addAll(Arrays.asList(pathNames));

    return keywords;
  }

}
