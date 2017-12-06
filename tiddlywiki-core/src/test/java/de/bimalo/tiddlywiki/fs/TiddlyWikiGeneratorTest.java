package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.common.CommandLineParser;
import org.junit.After;
import org.junit.AfterClass;
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
    public void TiddlyWikiGenerator_construct_ValidArguments() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx", "-templateFile=/test/test22/template.html", "-resultFile=/test/test22/result.html"};
        CommandLineParser cmdParser = new CommandLineParser();
        cmdParser.parseArguments(args);

        //TiddlyWikiGenerator twgen = new TiddlyWikiGenerator(cmdParser.getArgumentValues());
    }

}
