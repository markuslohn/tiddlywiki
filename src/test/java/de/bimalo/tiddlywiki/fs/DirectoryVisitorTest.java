package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.Tiddler;
import java.io.FileNotFoundException;
import java.util.Date;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
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
            directory = FileObjectFixture.getDirectoryFileObject("TestOrdner", "");

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
            fail("A FileNotFoundException is expected to be thrown.");
        } catch (FileNotFoundException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A IllegalArgumentException is expected to be thrown.");
        }

    }
}
