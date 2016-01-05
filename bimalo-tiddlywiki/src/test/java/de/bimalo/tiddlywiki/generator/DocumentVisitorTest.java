package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.generator.FileObjectVisitor;
import de.bimalo.tiddlywiki.generator.DocumentVisitor;
import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileContentInfo;
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
 * A test case for <code>DocumentVisitor</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see DocumentVisitor
 */
public class DocumentVisitorTest {

    public DocumentVisitorTest() {
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
    public void DocumentVisitor_visit_DocumentPropertiesNotAvailable() {
        FileObject document;
        try {
            document = getDocumentFileObject("test.txt", "/Referenz/A/", "Testcontent");

            FileObjectVisitor visitor = new DocumentVisitor();
            Object result = visitor.visit(document);
            assertNotNull(result);
            assertEquals(DocumentTiddler.class.getName(), result.getClass().getName());

            Tiddler tiddler = (Tiddler) result;
            assertEquals("test.txt", tiddler.getTitle());
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
    public void DocumentVisitor_Construct_InvalidFileObjectType() {
        try {
            Long lastModifiedTime = Long.valueOf(new Date().getTime());
            FileContent content = mock(FileContent.class);
            when(content.getLastModifiedTime()).thenReturn(lastModifiedTime);
            FileObject document = mock(FileObject.class);
            when(document.getContent()).thenReturn(content);
            when(document.getType()).thenReturn(FileType.FOLDER);

            FileObjectVisitor visitor = new DocumentVisitor();
            visitor.visit(document);
            fail("A FileNotFolderException is expected to be thrown.");
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A FileNotFolderException is expected to be thrown.");
        }
    }

    @Test
    public void DocumentVisitor_Construct_IllegalArgument() {
        try {
            FileObjectVisitor visitor = new DocumentVisitor();
            visitor.visit(null);
            fail("A IllegalArgumentException is expected to be thrown.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            fail("A IllegalArgumentException is expected to be thrown.");
        }

    }

    private FileObject getDocumentFileObject(String name, String path, String text) throws FileSystemException {
        /*DocumentProperties properties = mock(DocumentProperties.class);
         when(properties.getAuthor()).thenReturn(author);
         when(properties.getDescription()).thenReturn(description);
         when(properties.getSubject()).thenReturn(subject);
         when(properties.getTitle()).thenReturn(title);
         when(properties.getKeywords()).thenReturn(keywords);
         */
        FileContentInfo contentInfo = mock(FileContentInfo.class);
        when(contentInfo.getContentType()).thenReturn("xx");
        FileContent documentContent = mock(FileContent.class);
        when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes()));
        when(documentContent.getContentInfo()).thenReturn(contentInfo);

        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name);
        when(documentName.getBaseName()).thenReturn(name);

        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);
        try {
            when(document.getURL()).thenReturn(new URL("file://test/test.txt"));
        } catch (MalformedURLException ex) {
            new FileSystemException(ex);
        }

        return document;
    }

}
