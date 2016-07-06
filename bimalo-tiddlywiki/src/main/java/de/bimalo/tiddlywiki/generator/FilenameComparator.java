package de.bimalo.tiddlywiki.generator;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;

/**
 * <p>
 * A Comparator that uses the attributes base name and extension of a
 * <code>org.apache.commons.vfs2.FileObject</code> to sort a list.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see Tiddler
 */
final class FilenameComparator implements Comparator<FileObject>, Serializable {

    /**
     * Compares two <code>org.apache.commons.vfs2.FileObject</code> by using the
     * base name and the extension attributes.
     *
     * @param o1 FileObject to compare
     * @param o2 FileObject to compare
     * @return a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the
     *
     */
    public int compare(FileObject o1, FileObject o2) {
        String filename1 = getFilename(o1);
        String filename2 = getFilename(o2);
        return filename1.compareTo(filename2);
    }

    private String getFilename(final FileObject file) {
        FileName fileName = file.getName();
        StringBuilder sb = new StringBuilder();
        sb.append(fileName.getBaseName());
        sb.append(".");
        sb.append(fileName.getExtension());
        return sb.toString();
    }

}
