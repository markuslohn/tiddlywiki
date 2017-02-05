package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
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
            throw new FileNotFoundException("Null reference was provided as file");
        }
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }

        FileObjectProperties properties = getProperties4File(file);
        Tiddler tiddler = createTiddler4File(file, properties);

        return tiddler;
    }

    /**
     * Creates a new Tiddler based on the given FileObject and
     * FileObjectProperties.
     *
     * @param file the reference to the file
     * @param properties the properties of the file
     * @return the new Tiddler
     * @throws FileSystemException if operation fails
     * @throws IOException if operation fails
     */
    private Tiddler createTiddler4File(FileObject file, FileObjectProperties properties)
            throws FileSystemException, IOException {
        LOGGER.trace("Create tiddler for file {}...", file.getName().getPath());

        DocumentTiddler tiddler = new DocumentTiddler();
        tiddler.setName(file.getName().getBaseName());
        tiddler.setTitle(properties.getTitle());
        tiddler.setCreator(properties.getAuthor());
        tiddler.setModifier(properties.getAuthor());
        tiddler.setCreateDate(properties.getLastModifyDate());
        tiddler.addTags(filterKeywords(file, properties));
        tiddler.setText(buildText(file, properties));

        try {
            String uristr = URLEncoder.encode(file.getName().getURI(), "UTF8");
            tiddler.setUri(new URI(uristr));
        } catch (URISyntaxException ex) {
            throw new IOException(ex);
        }

        LOGGER.trace("Done create tiddler for file {}...", file.getName().getPath());

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(tiddler.toString());
        }

        return tiddler;
    }

    /**
     * Builds the text for a Tiddler based on the given document represented by
     * a FileObject.
     *
     * @param file the FileObject represents a document
     * @param properties meta data of the document
     * @return the generated text
     */
    private String buildText(final FileObject file, final FileObjectProperties properties) {
        StringBuilder text = new StringBuilder();
        text.append("[[");
        text.append(file.getName().getBaseName()).append("|").append(file.getName().getURI());
        text.append("]]\n");
        text.append("\n");
        String description = properties.getDescription();
        if (description != null && !description.isEmpty()) {
            text.append(description);
            text.append("\n\n");
        }

        String content = properties.getText();
        if (content != null && !content.isEmpty()) {
            text.append(content);
        }
        text.append("\n");

        return text.toString();
    }

    /**
     * Gets all necessary properties for the given <code>FileObject</code>.
     *
     * @param file a FileObject representing a file
     * @return the FileObjectProperties for the given FileObject.
     * @throws IOException if operation failed
     */
    private FileObjectProperties getProperties4File(final FileObject file) throws IOException {
        FileObjectProperties properties = null;

        LOGGER.trace("Read properties for file {}...", file.getName().getPath());
        String contentType = file.getContent().getContentInfo().getContentType();
        LOGGER.trace("Content-Type= {}.", contentType);
        if ("text/plain".equalsIgnoreCase(contentType)) {
            properties = new TextDocumentProperties(file);
        } else {
            properties = new BinaryDocumentProperties(file);
        }
        properties.reload();
        LOGGER.trace("Done reading properties.");

        return properties;
    }

    /**
     * Filters the keywords of a given document.
     *
     * @param file the document
     * @param properties the properties of the document, e.g. keywords
     * @return a list with filtered keywords
     * @throws FileSystemException if operation failed
     */
    private List<String> filterKeywords(final FileObject file, final FileObjectProperties properties)
            throws FileSystemException {
        List<String> keywords = new ArrayList(properties.getKeywords());

        FileObject parent = file.getParent();
        if (parent != null) {
            String parentName = parent.getName().getBaseName();
            keywords.remove(parentName);
        }

        return keywords;
    }

}
