package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.common.Assert;
import de.bimalo.tiddlywiki.common.StreamUtilities;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <code>FileObjectProperties</code> provides additional available information
 * about a <code>org.apache.commons.vfs2.FileObject</code>. It depends on the
 * type of a FileObject (directory or document) which information can be made
 * available.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see org.apache.commons.vfs2.FileObject
 */
abstract class FileObjectProperties {

  /**
   * Logger definition for this object.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectProperties.class);

  /**
   * The reference to the <code>org.apache.commons.vfs2.FileObject</code>.
   */
  private FileObject file = null;

  /**
   * The author of the FileObject.
   */
  private String author = null;
  /**
   * The title of the FileObject.
   */
  private String title = null;
  /**
   * The subject of the FileObject.
   */
  private String subject = null;
  /**
   * A textual description of the FileObject.
   */
  private String description = null;
  /**
   * A java.util.List with Strings representing keywords.
   */
  private List<String> keywords = new ArrayList<String>();
  /**
   * The date and time when the FileObject was last modified.
   */
  private Date lastModifyDate = null;
  /**
   * Content of the FileObject.
   */
  private String text = null;

  /**
   * The encoding used by the FileObject.
   */
  private String encoding = null;

  /**
   * The content type of the FileObject, like application/pdf.
   */
  private String contentType = null;

  /**
   * Creates a new <code>FileObjectProperties</code> based on a given
   * <code>org.apache.commons.vfs2.FileObject</code>.
   *
   * @param file FileObject representing a document or directory.
   * @exception IllegalArgumentException if file is null
   * @exception IOException if FileObjectProperties cannot be initialized
   */
  protected FileObjectProperties(FileObject file) throws IOException {
    Assert.notNull(file);
    this.file = file;
    lastModifyDate = new Date(file.getContent().getLastModifiedTime());
    title = file.getName().getBaseName();
    FileContent content = file.getContent();
    if (content != null) {
      FileContentInfo contentInfo = content.getContentInfo();
      if (contentInfo != null) {
        encoding = contentInfo.getContentEncoding();
        contentType = contentInfo.getContentType();
      }
    }
    String ext = file.getName().getExtension();
    if (ext != null && ext.equalsIgnoreCase("md")) {
      contentType = "text/x-markdown";
    }
    if (contentType == null || contentType.isEmpty() || (ext != null && ext.equalsIgnoreCase("txt"))) {
      contentType = "text/vnd.tiddlywiki";
    }
    if (encoding == null || encoding.isEmpty()) {
      encoding = java.nio.charset.Charset.defaultCharset().toString();
    }
  }

  /**
   * Gets the author attribute of the FileObject.
   *
   * @return the author as String or null if author is not available
   */
  public String getAuthor() {
    return author;
  }

  /**
   * Gets the date and time when the FileObject was modified.
   *
   * @return last modify date
   */
  public Date getLastModifyDate() {
    return lastModifyDate;
  }

  /**
   * Gets the title of the FileObject.
   *
   * @return the title as String
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the subject attribute of the FileObject.
   *
   * @return the subject as String or null if subject is not available
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Gets the description attribute of the FileObject.
   *
   * @return the description as String or null if description is not available
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the content of the FileObject.
   *
   * @return the content as String
   */
  public String getText() {
    return text;
  }

  /**
   * Gets a <code>java.util.List</code> with String objects representing
   * keywords for the FileObject.
   *
   * @return a <code>java.util.List</code> with String or an empty List if no
   * keyword available
   */
  public List<String> getKeywords() {
    return Collections.unmodifiableList(keywords);
  }

  /**
   * Gets the character encoding used by the FileObject.
   *
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Return the content type of the FileObject.
   * @return the content type.
   */
  public String getContentType() {
    return contentType;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(file.getName().getBaseName()).append("\n");
    sb.append(" [");
    try {
      sb.append("Type=").append(file.getType().getName()).append("\n");
    } catch (FileSystemException ex) {
      // can be ignore!
    }
    sb.append("Path=").append(file.getName().getPath()).append("\n");
    sb.append("Title=").append(title).append("\n");
    sb.append("ModifyDate=").append(lastModifyDate).append("\n");
    sb.append("]");
    return sb.toString();
  }

  /**
   * Reloads the properties from the corresponding FileObject.
   *
   * @throws IOException if reload failed
   */
  public void reload() throws IOException {
    loadMetadata(file);
  }

  /**
   * Loads the meta data for the given FileObject.
   *
   * @param file a directory or a file reference
   * @throws IOException if operations failed
   */
  protected void loadMetadata(FileObject file) throws IOException {
    FileContent content = file.getContent();
    LineNumberReader lnr = null;

    LOGGER.debug("Load meta data for file {}...", file.getName().getPath());
    try {
      lnr = new LineNumberReader(new InputStreamReader(content.getInputStream(), getEncoding()));
      String line = "";

      while ((line = lnr.readLine()) != null) {
        LOGGER.trace("Read line {}", line);

        int lineNr = lnr.getLineNumber();
        switch (lineNr) {
          /*
                     * First line does contain the title of the directory
           */
          case 1:
            parseTitle(line);
            break;

          /*
                     * The second line contains the tags as comma separated
                     * list
           */
          case 2:
            parseKeywords(line);
            break;
          /*
                     * Beginning with line 3 all text is appended to the
                     * description
           */
          default:
            addDescription(line);
            addText(line);
        }
      }
    } finally {
      StreamUtilities.closeReader(lnr);
      if (content != null) {
        content.close();
      }
    }
    LOGGER.trace("Done loading meta data for file {}.", file.getName().getPath());

  }

  /**
   * Finds a file, relative to this FileObjectProperties.
   *
   * @param path The path of the file to locate. Can either be a relative path
   * or an absolute path.
   * @return the FileObject
   * @throws IOException if operation failed
   */
  protected FileObject resolveFile(String path) throws IOException {
    return file.resolveFile(path);
  }

  protected void setAuthor(String author) {
    this.author = author;
  }

  protected void setTitle(String title) {
    if (title != null && !title.isEmpty()) {
      this.title = title;
    }
  }

  protected void setSubject(String subject) {
    this.subject = subject;
  }

  protected void setDescription(String description) {
    this.description = description;
  }

  protected void addDescription(String description) {
    if (description != null && !description.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      if (this.description != null) {
        sb.append(this.description);
        sb.append("\n");
      }
      sb.append(description);
      this.description = sb.toString();
    }
  }

  protected void addKeyword(String keyword) {
    if (keyword != null && !keywords.contains(keyword)) {
      keywords.add(keyword);
    }
  }

  protected void setText(String text) {
    this.text = text;
  }

  protected void addText(String text) {
    if (text != null && !text.isEmpty()) {
      StringBuilder sb = new StringBuilder();
      if (this.text != null) {
        sb.append(this.text);
        sb.append("\n");
      }
      sb.append(text);
      this.text = sb.toString();
    }
  }

  /**
   * Parses the "title" line. In this case the entire line content is used as
   * title.
   *
   * @param line line from the text file
   */
  protected void parseTitle(String line) {
    if (line != null && !line.isEmpty()) {
      setTitle(line);
    }
  }

  /**
   * Parses the "keywords" line. In this case the keywords are separated by
   * comma.
   *
   * @param line line from the text file
   */
  protected void parseKeywords(String line) {
    if (line != null && !line.isEmpty()) {
      StringTokenizer csKeywordsTokenizer = new StringTokenizer(line, ",");
      while (csKeywordsTokenizer.hasMoreTokens()) {
        String keyword = csKeywordsTokenizer.nextToken();
        keyword = keyword.trim();
        LOGGER.trace("keyword= {}.", keyword);
        if (keyword != null && !keyword.isEmpty()) {
          addKeyword(keyword);
        }
      }
    }
  }

  /**
   * Parses the description lines. All lines starting with number 3 are
   * appended to the description.
   *
   * @param line line from the text file
   */
  protected void parseDescription(String line) {
    addDescription(line);
  }
}
