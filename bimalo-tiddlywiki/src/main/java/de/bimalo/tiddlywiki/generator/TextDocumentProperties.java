package de.bimalo.tiddlywiki.generator;

import java.io.IOException;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;

/**
 * <p>
 * <code>TextDocumentProperties</code> is responsible for determine additional
 * meta data for a simple text file.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version 1.0
 * @since 1.7
 */
class TextDocumentProperties extends FileObjectProperties {

    /**
     * Creates a new <code>TextDocumentProperties</code> based on a given
     * <code>org.apache.commons.vfs2.FileObject</code>. The FileObject have to
     * be of type file.
     *
     * @param file a FileObject representing a file
     * @exception IllegalArgumentException if file is null
     * @exception IOException if file is not of type file
     */
    protected TextDocumentProperties(FileObject file) throws IOException {
        super(file);
        if (!(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file);
        }
    }

}