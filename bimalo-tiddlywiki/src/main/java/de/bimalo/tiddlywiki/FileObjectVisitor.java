package de.bimalo.tiddlywiki;

import java.io.IOException;
import org.apache.commons.vfs2.FileObject;

/**
 * <p>
 * Defines "visitor" operations for <code>org.apache.commons.vfs2.FileObject</code> objects. 
 * It implements the Visitor pattern of the GoF.</p>
 * <p>
 * A visitor represents an operation to be performed on every FileObject.
 * Visitor lets you define a new operation without changing the FileObject classes.</p>
 * 
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 */
public interface FileObjectVisitor {

    /**
     * "Visits" the provided <code>org.apache.commons.vfs2.FileObject</code> and executes
     * custom functions.
     * @param file execute the visit operation on this FileObject
     * @return the result of the visit 
     * @throws IOException if visit operation fails
     */
    public Object visit(FileObject file) throws IOException;
}
