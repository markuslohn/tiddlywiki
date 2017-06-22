package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.fs.FilesystemTreeWalker;
import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.TiddlyWiki;
import java.io.IOException;
import java.util.List;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * A test case for <code>FilesystemTreeWalkerTest</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see FilesystemTreeWalkerTest
 */
public class FilesystemTreeWalkerTest {

  public FilesystemTreeWalkerTest() {
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
  public void FilesystemTreeWalker_construct_NullArgument() {
    try {
      FilesystemTreeWalker tw = new FilesystemTreeWalker(null);
      fail("A IllegalArgumentException is expected to be thrown.");
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      assertTrue(ex.getMessage(), false);
    }
  }

  @Test
  public void FilesystemTreeWalker_construct_FileObjectDoesNotExist() {
    try {
      FileObject file = FileObjectFixture.getDirectoryFileObject("ordner_a", "", false, false);

      FilesystemTreeWalker tw = new FilesystemTreeWalker(file);
      fail("A IllegalArgumentException is expected to be thrown.");
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      assertTrue(ex.getMessage(), false);
    }
  }

  @Test
  public void FilesystemTreeWalker_construct_FileObjectNotAFolder() {
    try {
      FileObject file = FileObjectFixture.getDocumentFileObject("test.txt", "xx", true);

      FilesystemTreeWalker tw = new FilesystemTreeWalker(file);
      fail("A FileNotFolderException is expected to be thrown.");
    } catch (FileNotFolderException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      assertTrue(ex.getMessage(), false);
    }
  }

  @Test
  public void FilesystemTreeWalker_walkFileTree_Filesystem1() {
    try {
      FileObject rootFolder = FileObjectFixture.getFilesystem1();
      FilesystemTreeWalker tw = new FilesystemTreeWalker(rootFolder);
      TiddlyWiki wiki = tw.walkFileTree();
      assertNotNull(wiki);
      assertNotNull(wiki.getTitleTiddler());
      assertNotNull(wiki.getDefaultTiddler());

      List<Tiddler> tiddlers = wiki.listTiddlers();
      assertTrue(tiddlers.size() >= 1);
    } catch (FileSystemException ex) {
      assertTrue(ex.getMessage(), false);
    } catch (IOException ex) {
      assertTrue(ex.getMessage(), false);
    }

  }

}
