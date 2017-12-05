package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.common.StreamUtilities;
import de.bimalo.tika.parser.frontmatter.FrontMatterParser;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.tika.metadata.Property;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.Parser;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentVisitor.class);

    /**
     * The maximum number of characters for extracted text.
     */
    private static final int DEFAULT_MAXSTRINGLENGTH = 2048;

    @Override
    public Object visit(final FileObject file) throws IOException {
        if (file == null) {
            throw new FileNotFoundException("Parameter file (a file) is null!");
        }
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }

        LOGGER.debug("Visit file {}.", file.getName().getPath());
        Metadata md = new Metadata();
        String text = parseFile(file, md);

        Tiddler tiddler = createTiddler(file, md, text);

        return tiddler;
    }

    /**
     * Parses a given file and returns the content of the file and the meta
     * data.
     *
     * @param file a FileObject representing a file
     * @param md represents the meta data with key/values pairs
     * @return the extracted text of the file
     * @throws FileSystemException if content of the file could not be read
     */
    private String parseFile(final FileObject file, Metadata md) throws FileSystemException {
        String text = null;
        InputStream is = new BufferedInputStream(file.getContent().getInputStream());
        try {
            Tika ts = null;
            if (file.getName().getExtension().matches(FilesystemTreeWalker.TEXT_FILE_TYPES)) {
                Parser parser = new FrontMatterParser();
                ts = new Tika(TikaConfig.getDefaultConfig().getDetector(), parser);
            } else {
                ts = new Tika();
                ts.setMaxStringLength(DEFAULT_MAXSTRINGLENGTH);
            }
            text = ts.parseToString(is, md);
        } catch (Exception ex) {
            if (ex instanceof FileSystemException) {
                throw (FileSystemException) ex;
            } else {
                throw new FileSystemException(ex);
            }
        } finally {
            StreamUtilities.closeInputStream(is);
        }
        return text;
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
    private Tiddler createTiddler(FileObject file, Metadata md, String text)
            throws FileSystemException, IOException {
        LOGGER.debug("Create tiddler for file {}...", file.getName().getPath());

        Tiddler tiddler = new Tiddler();
        tiddler.setTitle(getTitle(file, md));
        tiddler.setCreator(getAuthor(file, md));
        tiddler.setModifier(getAuthor(file, md));
        tiddler.setCreateDate(getLastModifyDate(file, md));
        tiddler.addTags(filterKeywords(file, md));
        tiddler.setText(text);
        tiddler.setPath(file.getName().getPath());
        tiddler.setContentType(getContentType(file, md));

        LOGGER.debug("Done create tiddler for file {}...", file.getName().getPath());
        LOGGER.trace(tiddler.toString());

        return tiddler;
    }

    /**
     * Filters the keywords of a given document.
     *
     * @param file the reference to the file
     * @param md meta data of the file
     * @return a list with filtered keywords
     * @throws FileSystemException if operation failed
     */
    private List<String> filterKeywords(final FileObject file, final Metadata md) {
        List<String> keywords = new ArrayList();

        try {
            FileObject parent = file.getParent();
            if (parent != null) {
                String absolutePath = parent.getName().getPath();
                String[] pathNames = absolutePath.split(File.separator);
                keywords.addAll(Arrays.asList(pathNames));
            }
        } catch (FileSystemException ex) {
            LOGGER.warn(ex.getMessage());
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(ex.getMessage(), ex);
            }
        }
        keywords.addAll(getKeywordsForAttribute(file, md, TikaCoreProperties.KEYWORDS));
        keywords.addAll(getKeywordsForAttribute(file, md, "keywords"));

        return keywords;
    }

    private List<String> getKeywordsForAttribute(final FileObject file, final Metadata md, String attributeName) {
        List<String> keywords = new ArrayList();

        if (md.isMultiValued(attributeName)) {
            String[] values = md.getValues(attributeName);
            keywords.addAll(Arrays.asList(values));
        } else {
            String value = md.get(attributeName);
            if (value != null) {
                String[] values = value.split(",");
                keywords.addAll(Arrays.asList(values));
            }
        }
        return keywords;
    }

    private List<String> getKeywordsForAttribute(final FileObject file, final Metadata md, Property property) {
        List<String> keywords = new ArrayList();

        if (md.isMultiValued(property)) {
            String[] values = md.getValues(property);
            keywords.addAll(Arrays.asList(values));
        } else {
            String value = md.get(property);
            if (value != null) {
                String[] values = value.split(",");
                keywords.addAll(Arrays.asList(values));
            }
        }
        return keywords;
    }

    private String getTitle(final FileObject file, final Metadata md) {
        String title = md.get(TikaCoreProperties.TITLE);
        if (title == null) {
            title = md.get("title");
        }
        if (title == null) {
            title = file.getName().getBaseName();
        }
        return title;
    }

    private String getAuthor(final FileObject file, final Metadata md) {
        String author = md.get(TikaCoreProperties.CREATOR);
        if (author == null) {
            author = md.get(TikaCoreProperties.MODIFIER);
        }
        if (author == null) {
            author = md.get("author");
        }
        return author;
    }

    private Date getLastModifyDate(final FileObject file, final Metadata md) {
        Date lastModifyDate = null;
        try {
            lastModifyDate = new Date(file.getContent().getLastModifiedTime());
        } catch (FileSystemException ex) {
            LOGGER.warn(ex.getMessage());
            lastModifyDate = new Date();
        }
        return lastModifyDate;
    }

    private String getContentType(final FileObject file, final Metadata md) {
        String contentType = md.get(Metadata.CONTENT_TYPE);
        if (contentType == null) {
            String ext = file.getName().getExtension();
            if (ext != null && ext.matches(FilesystemTreeWalker.TEXT_FILE_TYPES)) {
                contentType = "text/x-markdown";
            }
        } else if (contentType.contains("markdown")) {
            contentType = "text/x-markdown";
        }
        if (contentType == null) {
            try {
                FileContent content = file.getContent();
                if (content != null) {
                    FileContentInfo contentInfo = content.getContentInfo();
                    if (contentInfo != null) {
                        contentType = contentInfo.getContentType();
                    }
                }
            } catch (FileSystemException ex) {
                LOGGER.warn(ex.getMessage());
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(ex.getMessage(), ex);
                }
                if (contentType == null) {
                    contentType = "text/x-markdown";
                }
            }
        }
        return contentType;
    }
}
