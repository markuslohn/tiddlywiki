package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.common.Assert;
import de.bimalo.tiddlywiki.common.StreamUtilities;
import de.bimalo.tika.parser.frontmatter.FrontMatterParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.Parser;
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
public class FileObjectProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileObjectProperties.class);

    /**
     * The maximum number of characters for extracted text.
     */
    private static final int DEFAULT_MAXSTRINGLENGTH = 2048;

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
     * A List with Strings representing keywords.
     */
    private final List<String> keywords = new ArrayList<>();
    /**
     * The date and time when the FileObject was last modified.
     */
    private Date lastModifyDate = null;

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
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }
        
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
        if (ext != null && ext.matches(FilesystemTreeWalker.TEXT_FILE_TYPES)) {
            contentType = "text/x-markdown";
        }
        if (encoding == null || encoding.isEmpty()) {
            encoding = Charset.defaultCharset().displayName();
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
     *
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    @Override
    public String toString() {
        String lineFeed = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName().getBaseName()).append(lineFeed);
        sb.append(" [");
        try {
            sb.append("Type=").append(file.getType().getName()).append(lineFeed);
        } catch (FileSystemException ex) {
            // can be ignore!
        }
        sb.append("Path=").append(file.getName().getPath()).append(lineFeed);
        sb.append("Title=").append(title).append(lineFeed);
        sb.append("ModifyDate=").append(lastModifyDate).append(lineFeed);
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
     * Parses the meta data of the given file.
     *
     * @param file a directory or a file reference
     * @throws IOException if operations failed
     */
    protected void loadMetadata(FileObject file) throws IOException {
        InputStream is = file.getContent().getInputStream();
        try {
            LOGGER.debug("Load meta data for file {}...", file.getName().getPath());
            Metadata md = new Metadata();

            Tika ts = null;
            if (file.getName().getExtension().matches(FilesystemTreeWalker.TEXT_FILE_TYPES)) {
                Parser parser = new FrontMatterParser();
                ts = new Tika(TikaConfig.getDefaultConfig().getDetector(), parser);
            } else {
                ts = new Tika();
                ts.setMaxStringLength(DEFAULT_MAXSTRINGLENGTH);

            }
            ts.parseToString(is, md);

            parseTitle(md);
            parseAuthor(md);
            parseDescription(md);
            parseKeywords(md);
            LOGGER.trace("Done loading meta data for file {}.", file.getName().getPath());
        } catch (Throwable th) {
            LOGGER.warn("Could not load metadata for file {}, because of {}.",
                    file.getName().getPath(), th.getMessage());
            LOGGER.trace(th.getMessage(), th);
        } finally {
            StreamUtilities.closeInputStream(is);
        }

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

    /**
     * Determines the author attribute from the document.
     *
     * @param md the meta data of the document
     */
    private void parseAuthor(Metadata md) {
        author = md.get(TikaCoreProperties.CREATOR);
    }

    /**
     * Determines the title attribute from the document. title.
     *
     * @param md the meta data of the document
     */
    private void parseTitle(Metadata md) {
        String tmpTitle = md.get(TikaCoreProperties.TITLE);
        if (tmpTitle != null) {
            title = tmpTitle;
        }
    }

    /**
     * Determines the keywords from the document
     *
     * @param md the meta data of the document
     */
    private void parseKeywords(Metadata md) {
        if (md.isMultiValued(TikaCoreProperties.KEYWORDS)) {
            String[] values = md.getValues(TikaCoreProperties.KEYWORDS);
            keywords.addAll(Arrays.asList(values));
        } else {
            String value = md.get(TikaCoreProperties.KEYWORDS);
            keywords.add(value);
        }
    }

    /**
     * Determines the description of the document.
     *
     * @param md the meta data of the document
     */
    private void parseDescription(Metadata md) {
        description = md.get(TikaCoreProperties.DESCRIPTION);
    }

}
