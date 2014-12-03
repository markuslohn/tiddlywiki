package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import de.bimalo.common.IOUtilities;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileObject;
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
 * @since 1.0
 * @see org.apache.commons.vfs2.FileObject
 */
abstract class FileObjectProperties {

    /**
     * Logger definition for this object.
     */
    private Logger logger = LoggerFactory.getLogger(FileObjectProperties.class);

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
     * Creates a new <code>FileObjectProperties</code> based on a given
     * <code>org.apache.commons.vfs2.FileObject</code>.
     *
     * @param file FileObject representing a document or directory.
     * @exception IllegalArgumentException if file is null
     * @IOException if FileObjectProperties cannot be initialized
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
            }
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
        List<String> copiedKeywords = new ArrayList<String>(keywords);
        return copiedKeywords;
    }

    /**
     * Gets the character encoding used by the FileObject.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Directory: ").append(file.getName().getPath()).append("\n");
        sb.append("Title=").append(title).append("\n");
        sb.append("Keywords=").append(keywords).append("\n");
        sb.append("Description=").append(description).append("\n");
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
        try {
            lnr = new LineNumberReader(new InputStreamReader(content.getInputStream(), getEncoding()));
            String line = "";

            while ((line = lnr.readLine()) != null) {
                logger.trace("Read line {}", line);

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
                        /**
                         * 28.11.2014 mlohn: The description attribute is moved
                         * to the text attribute. Due to this also the
                         * DirectoryVisitor was changed.
                         *
                         * parseDescription(line);
                         */
                        addText(line);
                }
            }
        } finally {
            IOUtilities.closeReader(lnr);
            if (content != null) {
                content.close();
            }
        }
    }

    /**
     * Finds a file, relative to this FileObjectProperties.
     *
     * @param path The path of the file to locate. Can either be a relative path
     * or an absolute path.
     * @return the FileObject
     * @exception IllegalArgumentException if path is null
     * @throws IOException if operation failed
     */
    protected FileObject resolveFile(String path) throws IOException {
        Assert.notNull(path);
        return file.resolveFile(path);
    }

    protected void setAuthor(String author) {
        this.author = author;
    }

    protected void setTitle(String title) {
        Assert.notNull(title);
        this.title = title;
    }

    protected void setSubject(String subject) {
        this.subject = subject;
    }

    protected void setDescription(String description) {
        this.description = description;
    }

    protected void addDescription(String description) {
        if (description == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (this.description != null) {
            sb.append(this.description);
            sb.append("\n");
        }
        sb.append(description);
        this.description = sb.toString();
    }

    protected void addKeyword(String keyword) {
        if (keyword == null || keywords.contains(keyword)) {
            return;
        }
        keywords.add(keyword);
    }

    protected void setText(String text) {
        this.text = text;
    }

    protected void addText(String text) {
        if (text == null) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        if (this.text != null) {
            sb.append(this.text);
            sb.append("\n");
        }
        sb.append(text);

        this.text = sb.toString();
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
        if (line == null || line.isEmpty()) {
            return;
        } else {
            StringTokenizer csKeywordsTokenizer = new StringTokenizer(line, ",");
            while (csKeywordsTokenizer.hasMoreTokens()) {
                String keyword = csKeywordsTokenizer.nextToken();
                keyword = keyword.trim();
                logger.trace("keyword= {}.", keyword);
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
