package de.bimalo.tiddlywiki;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * A test case for <code>TiddlyWiki</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @see TiddlyWiki
 */
public class TiddlyWikiTest {

    public TiddlyWikiTest() {
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
    public void TiddlyWiki_construct_DefaultValues() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        Assert.assertEquals("", tw.getTitle());
        Assert.assertEquals("", tw.getSubTitle());
        Assert.assertEquals(0, tw.listTiddlers().size());
    }

    @Test
    public void TiddlyWiki_setTitle_Unmodifiable() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        tw.setTitle("Test1");
        String title = tw.getTitle();
        Assert.assertEquals("Test1", title);
        Assert.assertEquals("", tw.getSubTitle());
        Assert.assertEquals(0, tw.listTiddlers().size());

        title = "Test2";
        String title2 = tw.getTitle();
        Assert.assertEquals("Test1", title2);
    }

    @Test
    public void TiddlyWiki_setTitle_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        tw.setTitle("test1");
        tw.setTitle(null);
        assertEquals(tw.getTitle(), "test1");
        tw.setTitle("");
        assertEquals(tw.getTitle(), "test1");
    }

    @Test
    public void TiddlyWiki_setSubTitle_Unmodifiable() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        tw.setSubtitle("SubTitle1");
        String subTitle = tw.getSubTitle();
        Assert.assertEquals("SubTitle1", subTitle);
        Assert.assertEquals(0, tw.listTiddlers().size());

        subTitle = "Test2";
        String subTitle2 = tw.getSubTitle();
        Assert.assertEquals("SubTitle1", subTitle2);
    }

    @Test
    public void TiddlyWiki_setSubTitle_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        tw.setSubtitle("test1");
        tw.setSubtitle(null);
        assertEquals(tw.getSubTitle(), "test1");
        tw.setTitle("");
        assertEquals(tw.getSubTitle(), "test1");
    }

    @Test
    public void TiddlyWiki_addTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        tw.addTiddler(null);
        assertEquals(0, tw.listTiddlers().size());
    }

    @Test
    public void TiddlyWiki_addDefaultTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        tw.addDefaultTiddler(null);
        assertNotNull(tw.getDefaultTiddler());
    }

    @Test
    public void TiddlyWiki_addMainMenuTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        tw.addMainMenuTiddler(null);
        assertNotNull(tw.getMainMenuTiddler());
    }

    @Test
    public void TiddlyWiki_addMainMenuTiddler_ParentIsNull() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);

        Tiddler t1 = new Tiddler("Test1");
        tw.addMainMenuTiddler(t1);

        Tiddler mainMenuTiddler = tw.getMainMenuTiddler();
        assertNull(mainMenuTiddler.getParent());
    }

}
