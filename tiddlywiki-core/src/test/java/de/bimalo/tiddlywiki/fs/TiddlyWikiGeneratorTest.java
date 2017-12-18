package de.bimalo.tiddlywiki.fs;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * A test case for <code>TiddlyWikiGenerator</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see TiddlyWikiGenerator
 */
public class TiddlyWikiGeneratorTest {

    public TiddlyWikiGeneratorTest() {
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
    public void TiddlyWikiGenerator_construct_InvalidArguments() {
        String[] args = new String[]{};
        try {
            TiddlyWikiGenerator.main(null);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }

    @Test
    public void TiddlyWikiGenerator_construct_InvalidArguments1() {
        String[] args = new String[]{};
        try {
            TiddlyWikiGenerator.main(args);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }

    @Test
    public void TiddlyWikiGenerator_construct_InvalidArguments2() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx"};
        try {
            TiddlyWikiGenerator.main(args);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }

    @Test
    public void TiddlyWikiGenerator_construct_InvalidArguments3() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx", "-templateFile=/tmp/template.html"};
        try {
            TiddlyWikiGenerator.main(args);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }

    @Test
    public void TiddlyWikiGenerator_construct_InvalidArguments4() {
        String[] args = new String[]{"-configFile=test.properties"};
        try {
            TiddlyWikiGenerator.main(args);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }

    @Test
    public void TiddlyWikiGenerator_construct_RootFolderDoesNotExist() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx", "-templateFile=/tmp/template.html", "-resultFile=test.html"};
        try {
            TiddlyWikiGenerator.main(args);
            assertTrue(false);
        } catch (Throwable th) {
            assertTrue(true);
        }
    }
}
