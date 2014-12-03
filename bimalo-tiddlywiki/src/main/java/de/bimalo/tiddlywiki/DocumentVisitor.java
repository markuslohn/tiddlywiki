package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import java.io.IOException;
import java.util.List;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * <p>
 * Implements the <code>FileObjectVisitor</code> interface for a
 * file represented by a given <code>org.apache.commons.vfs2.FileObject</code>.</p>
 * <p>
 * It creates a <code>Tiddler</code> based on the current file/document.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see Tiddler
 */
public final class DocumentVisitor implements FileObjectVisitor {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(DocumentVisitor.class);

    @Override
    public Object visit(FileObject file) throws IOException {
        Assert.notNull(file);
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }
        logger.trace("Gets FileObjectProperties for directory {}...", file.getName().getPath());
        FileObjectProperties properties = getFileObjectProperties(file);
        logger.trace("Done.");

        logger.trace("Create tiddler for document {}...", file.getName().getPath());
        Tiddler tiddler = new Tiddler();
        tiddler.setTitle(properties.getTitle());
        tiddler.setCreator(properties.getAuthor());
        tiddler.setModifier(properties.getAuthor());
        tiddler.setCreateDate(properties.getLastModifyDate());
        tiddler.addTags(filterKeywords(file, properties));
        tiddler.setText(buildText(file, properties));

        if (logger.isTraceEnabled()) {
            logger.trace(tiddler.toString());
        }

        logger.trace("Done.");

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
    private String buildText(FileObject file, FileObjectProperties properties) {
        StringBuilder text = new StringBuilder();
        text.append("[[").append(file.getName().getBaseName()).append("|").append(file.getName().getURI()).append("]]\n");
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
     * Gets the FileObjectProperties for the given FileObject.
     *
     * @param file the FileObject represents a document
     * @return the FileObjectProperties for the given FileObject.
     * @throws IOException if operation failed
     */
    private FileObjectProperties getFileObjectProperties(FileObject file) throws IOException {
        FileObjectProperties properties = null;
        
        String contentType = file.getContent().getContentInfo().getContentType();
        logger.trace("Content-Type= {}.", contentType);
        if ("text/plain".equalsIgnoreCase(contentType)) {
            properties = new TextDocumentProperties(file);
        } else {
            properties = new BinaryDocumentProperties(file);
        }
        properties.reload();
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
    private List<String> filterKeywords(FileObject file, FileObjectProperties properties) throws FileSystemException {
        List<String> keywords = properties.getKeywords();

        FileObject parent = file.getParent();
        if (parent != null) {
            String parentName = parent.getName().getBaseName();
            keywords.remove(parentName);
        }

        return keywords;
    }

}
