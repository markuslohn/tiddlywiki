package de.bimalo.tiddlywiki.fs;

import java.io.IOException;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * <code>DirectoryProperties</code> is responsible for determine additional meta
 * data for a directory/folder.</p>
 * <p>
 * Currently possible meta data of a directory are title, description and
 * keywords. This meta data are stored in a text file that has the same name as
 * the directory.
 * <br> For example:
 * <br> Additional meta data to the directory "project-a" have to be stored in a
 * text file with the name "project-a.txt".</p>
 * <p>
 * Syntax of the text file is as follows:
 * <br>
 * <ol>
 * <li>Line 1 contains the title </li>
 * <li>Line 2 contains the keywords as comma separated list </li>
 * <li>Line 3-x contains the description </li>
 * </ol></p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 */
class DirectoryProperties extends FileObjectProperties {

    /**
     * Logger definition for this object.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DirectoryProperties.class);

    /**
     * The name of the text file containing the meta data for the
     * directory/folder.
     */
    private String metadataFileName = null;

    /**
     * Creates a new <code>DirectoryProperties</code> based on a given
     * <code>org.apache.commons.vfs2.FileObject</code>. The FileObject have to
     * be of type directory/folder.
     *
     * @param directory a FileObject representing a directory/folder
     * @exception IllegalArgumentException if directory is null
     * @exception IOException if directory is not of type directory/folder
     */
    protected DirectoryProperties(FileObject directory) throws IOException {
        super(directory);
        if (!(directory.getType().equals(FileType.FOLDER))) {
            throw new FileNotFolderException(directory);
        }
        metadataFileName = buildMetadataFileName(directory);
    }

    /**
     * Builds the name of the text file containing the meta data for a given
     * FileObject.
     *
     * @param file the FileObject represents a directory
     * @return the name of the text file containing the meta data
     */
    private String buildMetadataFileName(FileObject file) {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getName().getBaseName());
        sb.append(".txt");
        return sb.toString();
    }

    @Override
    protected void loadMetadata(FileObject file) throws IOException {
        FileObject metadataFile = resolveFile(metadataFileName);
        if (metadataFile != null && metadataFile.exists()) {
            LOGGER.trace("metadata file {} exists.", metadataFile.getName().getPath());
            super.loadMetadata(metadataFile);
        }
    }

}
