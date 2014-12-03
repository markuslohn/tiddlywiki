package de.bimalo.tiddlywiki;

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
 * A test case for <code>DirectoryVisitor</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see DirectoryVisitor
 */
public class DirectoryVisitorTest {

    public DirectoryVisitorTest() {
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
    public void DirectoryVisitor_visit_DirectoryPropertiesNotAvailable() {
        FileObject directory;
        try {
            directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", "");

            FileObjectVisitor visitor = new DirectoryVisitor();
            Object result = visitor.visit(directory);
            assertNotNull(result);
            assertEquals(Tiddler.class.getName(), result.getClass().getName());

            Tiddler tiddler = (Tiddler) result;
            assertEquals("TestOrdner", tiddler.getTitle());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getCreateDate());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getId());
            assertNull(tiddler.getParent());

        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void DirectoryVisitor_visit_TitleFromMetadataFile() {
        FileObject directory;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner2").append("\n");
            String text = sb.toString();
            directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text, true);

            FileObjectVisitor visitor = new DirectoryVisitor();
            Object result = visitor.visit(directory);
            assertNotNull(result);
            assertEquals(Tiddler.class.getName(), result.getClass().getName());

            Tiddler tiddler = (Tiddler) result;
            assertEquals("TestOrdner2", tiddler.getTitle());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getCreateDate());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getId());
            assertNull(tiddler.getParent());
        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void DirectoryVisitor_visit_1TagsFromMetadataFile() {
        FileObject directory;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner2").append("\n");
            sb.append("A").append("\n");
            String text = sb.toString();
            directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text, true);

            FileObjectVisitor visitor = new DirectoryVisitor();
            Object result = visitor.visit(directory);
            assertNotNull(result);
            assertEquals(Tiddler.class.getName(), result.getClass().getName());

            Tiddler tiddler = (Tiddler) result;
            assertEquals("TestOrdner2", tiddler.getTitle());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getCreateDate());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getId());
            assertNull(tiddler.getParent());
            assertEquals(1, tiddler.getTags().size());

        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void DirectoryVisitor_visit_2TagsFromMetadataFile() {
        FileObject directory;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("TestOrdner2").append("\n");
            sb.append("A,B").append("\n");
            String text = sb.toString();
            directory = getDirectoryFileObject("TestOrdner", "/Referenz/T", text, true);

            FileObjectVisitor visitor = new DirectoryVisitor();
            Object result = visitor.visit(directory);
            assertNotNull(result);
            assertEquals(Tiddler.class.getName(), result.getClass().getName());

            Tiddler tiddler = (Tiddler) result;
            assertEquals("TestOrdner2", tiddler.getTitle());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getCreateDate());
            assertNotNull(tiddler.getModifier());
            assertNotNull(tiddler.getId());
            assertNull(tiddler.getParent());
            assertEquals(2, tiddler.getTags().size());
        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }
    }

    @Test
    public void DirectoryVisitor_Construct_InvalidFileObjectType() {
        try {
            Long lastModifiedTime = Long.valueOf(new Date().getTime());
            FileContent content = mock(FileContent.class);
            when(content.getLastModifiedTime()).thenReturn(lastModifiedTime);
            FileObject document = mock(FileObject.class);
            when(document.getContent()).thenReturn(content);
            when(document.getType()).thenReturn(FileType.FILE);

            FileObjectVisitor visitor = new DirectoryVisitor();
            visitor.visit(document);
            fail("A FileNotFolderException is expected to be thrown.");
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A FileNotFolderException is expected to be thrown.");
        }
    }

    @Test
    public void DirectoryVisitor_Construct_IllegalArgument() {
        try {
            FileObjectVisitor visitor = new DirectoryVisitor();
            visitor.visit(null);
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
