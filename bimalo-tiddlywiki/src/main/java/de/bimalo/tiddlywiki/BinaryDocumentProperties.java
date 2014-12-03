package de.bimalo.tiddlywiki;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.tika.Tika;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <code>BinaryDocumentProperties</code> is responsible for determine additional
 * meta data for a file.</p>
 * <p>
 * The meta data for a file will be extracted with Apache Tika framework.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see org.apache.tika.Tika
 */
class BinaryDocumentProperties extends FileObjectProperties {

    /**
     * Logger definition for this object.
     */
    private final Logger logger = LoggerFactory.getLogger(BinaryDocumentProperties.class);
    /**
     * Special logger for parsing errors with Apache Tika Parsers.
     */
    private final Logger notParsableDocumentsLogger = LoggerFactory.getLogger("de.bimalo.tiddlywiki.NotParsableDocuments");

    /**
     * The maximum number of characters for extracted text.
     */
    private final static int DEFAULT_MAXSTRINGLENGTH = 2048;

    /**
     * Creates a new <code>DocumentProperties</code> based on a given
     * <code>org.apache.commons.vfs2.FileObject</code>. The FileObject have to
     * be of type file.
     *
     * @param file a FileObject representing a file
     * @exception IllegalArgumentException if file is null
     * @exception IOException if file is not of type file
     */
    protected BinaryDocumentProperties(FileObject file) throws IOException {
        super(file);
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }
    }

    @Override
    protected void loadMetadata(FileObject file) throws IOException {
        /**
         * 23.06.2014 mlohn Cannot use vfs-API because the generated InputStream
         * cannot be used by Tika without stream closed exception.
         *
         * FileContent content = file.getContent(); InputStream is =
         * content.getInputStream();
         */
        InputStream is = new FileInputStream(file.getName().getPath());

        try {
            logger.trace("Load meta data for file {}...", file.getName().getPath());
            Metadata md = new Metadata();

            Tika tikaService = new Tika();
            tikaService.setMaxStringLength(DEFAULT_MAXSTRINGLENGTH);
            String text = tikaService.parseToString(is, md);
            parseText(text);

            parseTitle(md);
            parseSubject(md);
            parseAuthor(md);
            parseDescription(md);
            parseKeywords(md);
            logger.trace("Done.");
        } catch (Throwable th) {
            notParsableDocumentsLogger.info(file.getName().getPath());
            logger.error("DocumentProperties: Could not load metadata for file {}, because of {}.", file.getName().getPath(), th.getMessage());
            if (logger.isTraceEnabled()) {
                logger.trace(th.getMessage(), th);
            }
        }
    }

    /**
     * Determines the subject attribute from the document.
     *
     * @param md the meta data of the document
     */
    private void parseSubject(Metadata md) {
        String subject = md.get(Metadata.SUBJECT);
        if (subject != null && !subject.isEmpty()) {
            setSubject(subject);
        }
    }

    /**
     * Determines the author attribute from the document.
     *
     * @param md the meta data of the document
     */
    private void parseAuthor(Metadata md) {
        String author = md.get(TikaCoreProperties.CREATOR);
        if (author != null && !author.isEmpty()) {
            setAuthor(author);
        }
    }

    /**
     * Determines the title attribute from the document. title.
     *
     * @param md the meta data of the document
     */
    private void parseTitle(Metadata md) {
        String title = md.get(TikaCoreProperties.TITLE);
        super.parseTitle(title);
    }

    /**
     * Determines the keywords from the document
     *
     * @param md the meta data of the document
     */
    private void parseKeywords(Metadata md) {
        String keywords = md.get(TikaCoreProperties.KEYWORDS);
        super.parseKeywords(keywords);
    }

    /**
     * Determines the description of the document.
     *
     * @param md the meta data of the document
     */
    private void parseDescription(Metadata md) {
        String description = md.get(TikaCoreProperties.DESCRIPTION);
        super.parseDescription(description);
    }

    /**
     * Parses the extracted content of a document.
     *
     * @param text the extracted content
     */
    private void parseText(String text) {
        StringBuilder sb = new StringBuilder(text.trim());
        boolean found = false;
        for (int i = 0; i < sb.length(); i++) {
            found = false;
            if (sb.charAt(i) == '\n') {
                sb.deleteCharAt(i);
                found = true;
            }
            if (sb.charAt(i) == '\r') {
                sb.deleteCharAt(i);
                found = true;
            }
            if (sb.charAt(i) == '\t') {
                sb.deleteCharAt(i);
                found = true;
            }
            if (found) {
                i--;
            }

        }
        addText(sb.toString());
    }

}
