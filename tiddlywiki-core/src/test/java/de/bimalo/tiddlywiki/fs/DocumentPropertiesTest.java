package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.fs.BinaryDocumentProperties;
import de.bimalo.tiddlywiki.fs.FileObjectProperties;
import java.io.ByteArrayInputStream;
import java.util.Date;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>
 * A test case for <code>DocumentProperties</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see BinaryDocumentProperties
 */
public class DocumentPropertiesTest {

    public DocumentPropertiesTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void DocumentProperties_Construct_NoMetadata() {
        try {
            FileObject document = getDocumentFileObject("test.txt", "/Referenz/A/", "Testcontent");

            FileObjectProperties properties = new BinaryDocumentProperties(document);
            properties.reload();

            assertNull(properties.getAuthor());
            assertNull(properties.getDescription());
            assertNull(properties.getTitle());
        } catch (Exception ex) {
            ex.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    public void DocumentProperties_construct_ChangePropertiesOutside() {
        FileObject document;
        try {
            document = getDocumentFileObject("test.txt", "/Referenz/A/", "Testcontent");

            FileObjectProperties properties = new BinaryDocumentProperties(document);
            properties.reload();
            String author = properties.getAuthor();
            assertNull(author);
            author = "test1";
            String author1 = properties.getAuthor();
            assertNotEquals(author1, author);
        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }

    }

    @Test
    public void DocumentProperties_Construct_InvalidFileObjectType() {
        try {
            Long lastModifiedTime = Long.valueOf(new Date().getTime());
            FileName name = mock(FileName.class);
            when(name.getBaseName()).thenReturn("test");
            FileContent content = mock(FileContent.class);
            when(content.getLastModifiedTime()).thenReturn(lastModifiedTime);
            FileObject document = mock(FileObject.class);
            when(document.getContent()).thenReturn(content);
            when(document.getType()).thenReturn(FileType.FOLDER);
            when(document.getName()).thenReturn(name);

            FileObjectProperties properties = new BinaryDocumentProperties(document);
            fail("A FileNotFolderException is expected to be thrown.");
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A FileNotFolderException is expected to be thrown.");
        }
    }

    @Test
    public void DocumentProperties_Construct_IllegalArgument() {
        try {
            FileObjectProperties properties = new BinaryDocumentProperties(null);
            fail("A IllegalArgumentException is expected to be thrown.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A IllegalArgumentException is expected to be thrown.");
        }

    }

    private FileObject getDocumentFileObject(String name, String path, String text) throws FileSystemException {
        FileContent documentContent = mock(FileContent.class);
        when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes()));
        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name);
        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);

        return document;
    }

}
