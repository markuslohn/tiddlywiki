package de.bimalo.tika.parser.frontmatter;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

/**
 * <p>
 * A test case for <code>FrontMatterParser</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 */
public class FrontMatterParserTest {

  public FrontMatterParserTest() {
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
  public void testParseWithFrontMatter() {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("---\n");
      sb.append("title: testtitle\n");
      sb.append("tags: [a,b,c,noch ein tag]\n");
      sb.append("subtitle: untertitel\n");
      sb.append("sonstiges: xxx\n");
      sb.append("---\n");
      sb.append("First Line Description\n");
      sb.append("Second Line Description\n");
      String text = sb.toString();
      FileObject file = getTextDocumentFileObject("Testdokument", "/Referenz/T", text);

      Metadata metadata = new Metadata();
      ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
      FrontMatterParser fmp = new FrontMatterParser();

      Tika tikaService = new Tika(TikaConfig.getDefaultConfig().getDetector(), fmp);
      String content = tikaService.parseToString(is, metadata);
      assertNotNull(content);
      assertNotNull(metadata);
      assertEquals("testtitle", metadata.get("title"));
      assertTrue(metadata.isMultiValued("tags"));
      String[] tagValues = metadata.getValues("tags");
      assertEquals(4, tagValues.length);

      System.out.println(metadata);
      System.out.println(content);

    } catch (FileNotFolderException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  @Test
  public void testParseWithoutFrontMatter() {
    try {
      StringBuilder sb = new StringBuilder();
      sb.append("First Line Description\n");
      sb.append("Second Line Description\n");
      String text = sb.toString();
      FileObject file = getTextDocumentFileObject("Testdokument", "/Referenz/T", text);

      Metadata metadata = new Metadata();
      ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes());
      FrontMatterParser fmp = new FrontMatterParser();

      Tika tikaService = new Tika(TikaConfig.getDefaultConfig().getDetector(), fmp);
      String content = tikaService.parseToString(is, metadata);
      assertNotNull(content);
      assertNotNull(metadata);
      assertNull(metadata.get("title"));

      System.out.println(metadata);
      System.out.println(content);

    } catch (FileNotFolderException ex) {
      assertTrue(true);
    } catch (Exception ex) {
      ex.printStackTrace();
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
