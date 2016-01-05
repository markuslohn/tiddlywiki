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
 * @since 1.0
 * @see TiddlyWiki TODO: addXXX Testfälle ergänzen!
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
    
    public void TiddlyWiki_setTitle_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        try {
            tw.setTitle(null);
            fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
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
    
    public void TiddlyWiki_setSubTitle_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        try {
            tw.setSubtitle(null);
            fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }
    
    public void TiddlyWiki_addTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        try {
            tw.addTiddler(null);
            fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }
    
    public void TiddlyWiki_addDefaultTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        try {
            tw.addDefaultTiddler(null);
            fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }
    
  
    public void TiddlyWiki_addMainMenuTiddler_InvalidArgument() {
        TiddlyWiki tw = new TiddlyWiki();
        assertNotNull(tw);
        try {
            tw.addMainMenuTiddler(null);
            fail("IllegalArgumentException expected.");
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
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
