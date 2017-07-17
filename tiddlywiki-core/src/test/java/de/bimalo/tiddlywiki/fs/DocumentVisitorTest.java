package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.fs.DocumentVisitor;
import de.bimalo.tiddlywiki.fs.FileObjectVisitor;
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
 * A test case for <code>DocumentVisitor</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
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
  public void DocumentVisitor_visit_MarkdownDocument() {
    FileObject document;
    try {
      document = FileObjectFixture.getDocumentFileObject("test.md", "test.md\n\nTestcontent", true);

      FileObjectVisitor visitor = new DocumentVisitor();
      Object result = visitor.visit(document);
      assertNotNull(result);
      assertEquals(Tiddler.class.getName(), result.getClass().getName());

      Tiddler tiddler = (Tiddler) result;
      assertEquals("test.md", tiddler.getTitle());
      assertNotNull(tiddler.getModifier());
      assertNotNull(tiddler.getCreateDate());
      assertNotNull(tiddler.getModifier());
      assertNotNull(tiddler.getId());
      assertNull(tiddler.getParent());
      assertNotNull(tiddler.getContentType());
      assertEquals("text/x-markdown", tiddler.getContentType());
    } catch (Exception ex) {
      assertTrue(ex.getMessage(), false);
    }
  }

  @Test
  public void DocumentVisitor_visit_DocumentPropertiesNotAvailable() {
    FileObject document;
    try {
      document = FileObjectFixture.getDocumentFileObject("test.txt", "test.txt\n\nTestcontent", true);

      FileObjectVisitor visitor = new DocumentVisitor();
      Object result = visitor.visit(document);
      assertNotNull(result);
      assertEquals(Tiddler.class.getName(), result.getClass().getName());

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
      fail("A FileNotFoundException is expected to be thrown.");
    } catch (FileNotFoundException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      fail("A IllegalArgumentException is expected to be thrown.");
    }

  }
}
