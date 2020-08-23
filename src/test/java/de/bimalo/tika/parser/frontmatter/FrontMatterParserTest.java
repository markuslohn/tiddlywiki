package de.bimalo.tika.parser.frontmatter;

import org.apache.commons.vfs2.FileNotFolderException;
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
            sb.append("keywords: [a,b,c,noch ein tag]\n");
            sb.append("subtitle: untertitel\n");
            sb.append("sonstiges: xxx\n");
            sb.append("---\n");
            sb.append("First Line Description\n");
            sb.append("Second Line Description\n");
            String text = sb.toString();

            Metadata metadata = new Metadata();
            ByteArrayInputStream is = new ByteArrayInputStream(text.getBytes("UTF-8"));
            FrontMatterParser fmp = new FrontMatterParser();

            Tika tikaService = new Tika(TikaConfig.getDefaultConfig().getDetector(), fmp);
            String content = tikaService.parseToString(is, metadata);
            assertNotNull(content);
            assertNotNull(metadata);

            assertEquals("testtitle", metadata.get("title"));
            assertTrue(metadata.isMultiValued("keywords"));
            String[] tagValues = metadata.getValues("keywords");
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

            Metadata metadata = new Metadata();
            ByteArrayInputStream is = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));
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
}
