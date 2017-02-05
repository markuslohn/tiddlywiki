package de.bimalo.tiddlywiki.generator;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.List;
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
 * A test case for <code>TextDocumentProperties</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see TextDocumentProperties
 */
public class TextDocumentPropertiesTest {

    public TextDocumentPropertiesTest() {
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
    public void TextDocumentProperties_Construct_InvalidFileObjectType() {
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

            FileObjectProperties properties = new TextDocumentProperties(document);
            fail("A FileNotFolderException is expected to be thrown.");
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A FileNotFolderException is expected to be thrown.");
        }
    }

    @Test
    public void TextDocumentProperties_Construct_IllegalArgument() {
        try {
            FileObjectProperties properties = new TextDocumentProperties(null);
            fail("A IllegalArgumentException is expected to be thrown.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A IllegalArgumentException is expected to be thrown.");
        }
    }

    @Test
    public void TextDocumentProperties_reload_ReadPropertiesFromFile() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Test").append("\n");
            sb.append("A, B ,C,  D\n");
            sb.append("First Line Description\n");
            sb.append("Second Line Description\n");
            String text = sb.toString();
            FileObject file = getTextDocumentFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new TextDocumentProperties(file);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("Test", properties.getTitle());

            List<String> keywords = properties.getKeywords();
            assertEquals(4, keywords.size());
            assertEquals("B", keywords.get(1));
            assertEquals("D", keywords.get(3));
            try {
                keywords.clear();
                fail("Modification of the keywords is not alloed.");
            } catch (UnsupportedOperationException ex) {
                assertTrue(true);
            }

            String description = properties.getText();
            if (!description.contains("First") || !description.contains("Second")) {
                fail("Description is not as expected: " + description);
            }
            description = "Neue Beschreibung";
            String description2 = properties.getText();
            assertNotEquals(description, description2);

            Date lastModifyDate = properties.getLastModifyDate();
            lastModifyDate = new Date();
            Date lastModifyDate2 = properties.getLastModifyDate();
            assertNotEquals(lastModifyDate, lastModifyDate2);

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    private FileObject getTextDocumentFileObject(String name, String path, String text) throws FileSystemException {
        FileContent documentContent = mock(FileContent.class);
        when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes()));
        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name + ".txt");
        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);
        when(document.exists()).thenReturn(Boolean.TRUE);

        return document;
    }

}
