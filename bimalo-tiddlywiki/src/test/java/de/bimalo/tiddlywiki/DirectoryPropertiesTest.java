package de.bimalo.tiddlywiki;

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
import static org.mockito.Mockito.*;

/**
 * <p>
 * A test case for <code>DirectoryProperties</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see DirectoryProperties
 */
public class DirectoryPropertiesTest {

    public DirectoryPropertiesTest() {
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
    public void DirectoryProperties_Construct_NoMetadataFile() {
        try {
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", "", false);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            assertNull(properties.getDescription());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 0);

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_NoContentInMetadataFile() {
        try {
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", "");
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            assertNull(properties.getDescription());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 0);

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_1KeywordAndNoDescription() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("A").append("\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            assertNull(properties.getDescription());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 1);
            assertEquals(keywords.get(0), "A");

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }

    }

    @Test
    public void DirectoryProperties_Construct_2KeywordAndNoDescription() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("A, B").append("\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            assertNull(properties.getDescription());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 2);
            assertEquals(keywords.get(0), "A");
            assertEquals(keywords.get(1), "B");

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_0KeywordAndNoDescription() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            assertNull(properties.getDescription());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 0);

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_0KeywordAndDescription() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("\n");
            sb.append("First Line Description");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 0);
            assertEquals("First Line Description", properties.getDescription());

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_0KeywordAndMultipleDescriptionLines() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("\n");
            sb.append("First Line Description\n");
            sb.append("Second Line Description\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 0);
            String description = properties.getDescription();
            if (!description.contains("First") || !description.contains("Second")) {
                fail("Description is not as expected: " + description);
            }

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_4KeywordAndMultipleDescriptionLins() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("A, B ,C,  D\n");
            sb.append("First Line Description\n");
            sb.append("Second Line Description\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());
            List<String> keywords = properties.getKeywords();
            assertEquals(keywords.size(), 4);
            assertEquals("B", keywords.get(1));
            assertEquals("D", keywords.get(3));
            String description = properties.getDescription();
            if (!description.contains("First") || !description.contains("Second")) {
                fail("Description is not as expected: " + description);
            }

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_ChangePropertiesOutside() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner").append("\n");
            sb.append("A, B ,C,  D\n");
            sb.append("First Line Description\n");
            sb.append("Second Line Description\n");
            String text = sb.toString();
            FileObject directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text);
            FileObjectProperties properties = new DirectoryProperties(directory);
            properties.reload();

            assertNull(properties.getAuthor());
            assertEquals("TestOrdner", properties.getTitle());

            List<String> keywords = properties.getKeywords();
            assertEquals(4, keywords.size());
            assertEquals("B", keywords.get(1));
            assertEquals("D", keywords.get(3));
            keywords.clear();
            keywords.add("Test1");
            List<String> keywords2 = properties.getKeywords();
            assertEquals(4, keywords2.size());
            assertEquals("B", keywords2.get(1));

            String description = properties.getDescription();
            if (!description.contains("First") || !description.contains("Second")) {
                fail("Description is not as expected: " + description);
            }
            description = "Neue Beschreibung";
            String description2 = properties.getDescription();
            assertNotEquals(description, description2);

            Date lastModifyDate = properties.getLastModifyDate();
            lastModifyDate = new Date();
            Date lastModifyDate2 = properties.getLastModifyDate();
            assertNotEquals(lastModifyDate, lastModifyDate2);

        } catch (Exception ex) {
            fail(ex.getMessage() + " is not expected.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_InvalidFileObjectType() {
        try {
            Long lastModifiedTime = Long.valueOf(new Date().getTime());
            FileName name = mock(FileName.class);
            when(name.getBaseName()).thenReturn("test.txt");
            FileContent content = mock(FileContent.class);
            when(content.getLastModifiedTime()).thenReturn(lastModifiedTime);
            FileObject document = mock(FileObject.class);
            when(document.getContent()).thenReturn(content);
            when(document.getType()).thenReturn(FileType.FILE);
            when(document.getName()).thenReturn(name);

            FileObjectProperties properties = new DirectoryProperties(document);
            fail("A FileNotFolderException is expected to be thrown.");
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A FileNotFolderException is expected to be thrown.");
        }
    }

    @Test
    public void DirectoryProperties_Construct_IllegalArgument() {
        try {
            FileObjectProperties properties = new DirectoryProperties(null);
            fail("A IllegalArgumentException is expected to be thrown.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A IllegalArgumentException is expected to be thrown.");
        }

    }

    private FileObject getDirectoryFileObject(String name, String path, String text) throws FileSystemException {
        return getDirectoryFileObject(name, path, text, true);
    }

    private FileObject getDirectoryFileObject(String name, String path, String text, boolean metadataFileExists) throws FileSystemException {
        FileContent documentContent = mock(FileContent.class);
        when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes()));
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
        FileObject directory = mock(FileObject.class);
        when(directory.getName()).thenReturn(directoryName);
        when(directory.getContent()).thenReturn(directoryContent);
        when(directory.getType()).thenReturn(FileType.FOLDER);
        when(directory.resolveFile(name + ".txt")).thenReturn(document);
        when(directory.exists()).thenReturn(Boolean.TRUE);

        return directory;
    }

}
