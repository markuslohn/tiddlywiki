package de.bimalo.tiddlywiki.fs;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * A fixture to create <code>FileObject</code>s to used it in unit tests.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see org.apache.commons.vfs2.FileObject
 */
public class FileObjectFixture {

    /**
     * Creates a new FileObject of type FILE.
     *
     * @param name the name, including the extension, like "txt"
     * @param text the content of the file
     * @param exists true=exists, false= not exists
     * @return the new FileObject
     * @throws FileSystemException if operation fails
     */
    public static FileObject getDocumentFileObject(String name, String text, boolean exists) throws FileSystemException {
        FileObject parentFolder = getRootFolder();
        return getDocumentFileObject(parentFolder, name, text, exists);
    }

    /**
     * Creates a new FileObject of type FILE.
     *
     * @param parent the parent FileObject
     * @param name the name, including the extension, like "txt"
     * @param text the content of the file
     * @param exists true=exists, false= not exists
     * @return the new FileObject
     * @throws FileSystemException if operation fails
     */
    public static FileObject getDocumentFileObject(FileObject parent, String name, String text, boolean exists) throws FileSystemException {
        String path = parent.getName().getPath();
        String extension = getExtensionFromName(name);

        FileContentInfo contentInfo = mock(FileContentInfo.class);
        when(contentInfo.getContentType()).thenReturn("text/plain");
        FileContent documentContent = mock(FileContent.class);
        try {
            when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new FileSystemException(ex);
        }
        when(documentContent.getContentInfo()).thenReturn(contentInfo);

        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name);
        when(documentName.getBaseName()).thenReturn(name);
        when(documentName.getURI()).thenReturn("file://" + path + "/" + name);
        when(documentName.getExtension()).thenReturn(extension);
        FileName parentFileName = parent.getName();
        when(documentName.getParent()).thenReturn(parentFileName);

        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);
        when(document.exists()).thenReturn(exists);
        when(document.getParent()).thenReturn(parent);

        try {
            when(document.getURL()).thenReturn(new URL("file://" + path + "/" + name));
        } catch (MalformedURLException ex) {
            throw new FileSystemException(ex);
        }

        return document;
    }

    /**
     * Creates a new FileObject of type FOLDER.
     *
     * @param name the name
     * @param text the content for the meta data file
     * @return the new FileObject
     * @throws FileSystemException if operation fails
     */
    public static FileObject getDirectoryFileObject(String name, String text) throws FileSystemException {
        FileObject parentFolder = getRootFolder();
        return getDirectoryFileObject(parentFolder, name, text, true, true);
    }

    /**
     * Creates a new FileObject of type FOLDER.
     *
     * @param name the name
     * @param text the content for the meta data file
     * @param exists true=exists, false= not exists
     * @param metadataFileExists true=file exists, otherwise false
     * @return the new FileObject
     * @throws FileSystemException if operation fails
     */
    public static FileObject getDirectoryFileObject(String name, String text, boolean exists, boolean metadataFileExists) throws FileSystemException {
        FileObject parentFolder = getRootFolder();
        return getDirectoryFileObject(parentFolder, name, text, exists, metadataFileExists);
    }

    /**
     * Creates a new FileObject of type FOLDER.
     *
     * @param parent the parent FileObject
     * @param name the name
     * @param text the content for the meta data file
     * @param exists true=exists, false= not exists
     * @param metadataFileExists true=file exists, otherwise false
     * @return the new FileObject
     * @throws FileSystemException if operation fails
     */
    public static FileObject getDirectoryFileObject(FileObject parent, String name, String text, boolean exists, boolean metadataFileExists) throws FileSystemException {
        String path = parent.getName().getPath();

        FileContent documentContent = mock(FileContent.class);
        try {
            when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException ex) {
            throw new FileSystemException(ex);
        }
        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name + ".txt");
        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);
        if (metadataFileExists) {
            when(document.exists()).thenReturn(Boolean.TRUE);
        } else {
            when(document.exists()).thenReturn(Boolean.FALSE);
        }

        FileContent directoryContent = mock(FileContent.class);
        when(directoryContent.getLastModifiedTime()).thenReturn(Long.valueOf(new Date().getTime()));
        FileName directoryName = mock(FileName.class);
        when(directoryName.getBaseName()).thenReturn(name);
        when(directoryName.getPath()).thenReturn(path);
        FileName parentFileName = parent.getName();
        when(directoryName.getParent()).thenReturn(parentFileName);

        FileObject directory = mock(FileObject.class);
        when(directory.getName()).thenReturn(directoryName);
        when(directory.getContent()).thenReturn(directoryContent);
        when(directory.getType()).thenReturn(FileType.FOLDER);
        when(directory.resolveFile(name + ".txt")).thenReturn(document);
        when(directory.exists()).thenReturn(exists);
        when(directory.getParent()).thenReturn(parent);
        return directory;
    }

    public static FileObject getFilesystem1() throws FileSystemException {
        FileObject rootFolder = getRootFolder();

        FileObject a = getDirectoryFileObject(rootFolder, "A", "", true, false);
        FileObject b = getDirectoryFileObject(rootFolder, "B", "", true, false);
        when(rootFolder.getChildren()).thenReturn(new FileObject[]{a, b});

        FileObject adf = getDirectoryFileObject(a, "ADF", "", true, false);
        FileObject architecture = getDirectoryFileObject(a, "Architektur", "", true, false);
        when(a.getChildren()).thenReturn(new FileObject[]{adf, architecture});

        when(architecture.getChildren()).thenReturn(new FileObject[0]);

        FileObject doc1 = getDocumentFileObject(adf, "doc1.pdf", "Inhalt der Datei doc1", true);
        FileObject doc2 = getDocumentFileObject(adf, "doc2.TXT", "Inhalt der Datei doc2", true);
        FileObject doc3 = getDocumentFileObject(adf, "doc3.xyz", "Inhalt der Datei doc3", true);
        when(adf.getChildren()).thenReturn(new FileObject[]{doc1, doc2, doc3});

        FileObject bewerbung = getDirectoryFileObject(b, "Bewerbung", "", true, false);
        FileObject bpel = getDirectoryFileObject(b, "BPEL", "", true, false);
        when(b.getChildren()).thenReturn(new FileObject[]{bewerbung, bpel});

        FileObject doc4 = getDocumentFileObject(bpel, "doc4.docx", "Inhalt der Datei doc4", true);
        FileObject doc5 = getDocumentFileObject(bpel, "doc5.itmz", "Inhalt der Datei doc5", true);
        when(bpel.getChildren()).thenReturn(new FileObject[]{doc4, doc5});

        when(bewerbung.getChildren()).thenReturn(new FileObject[0]);

        return rootFolder;
    }

    /**
     * Creates a mock for the root folder.
     *
     * @return the FileObject representing the root folder
     * @throws FileSystemException if operation fails
     */
    private static FileObject getRootFolder() throws FileSystemException {
        FileName directoryName = mock(FileName.class);
        when(directoryName.getBaseName()).thenReturn("Referenz");
        when(directoryName.getPath()).thenReturn("/home/documents");
        when(directoryName.getParent()).thenReturn(null);

        FileContent directoryContent = mock(FileContent.class);
        when(directoryContent.getLastModifiedTime()).thenReturn(Long.valueOf(new Date().getTime()));

        FileObject directory = mock(FileObject.class);
        when(directory.getName()).thenReturn(directoryName);
        when(directory.getType()).thenReturn(FileType.FOLDER);
        when(directory.exists()).thenReturn(true);
        when(directory.getContent()).thenReturn(directoryContent);

        return directory;
    }

    private static String getExtensionFromName(String name) {
        String extension = null;
        int indx = name.lastIndexOf('.');
        if (indx > 0) {
            extension = name.substring(indx + 1);
        }
        return extension;
    }
}
