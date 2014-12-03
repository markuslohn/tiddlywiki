package de.bimalo.tiddlywiki;

import java.rmi.server.UID;
import java.util.Date;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * A test case for <code>Tiddler</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see Tiddler
 */
public class TiddlerTest {

    public TiddlerTest() {
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
    public void Tidder_construct_InvalidArgument() {
        try {
            Tiddler t = new Tiddler(null);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void Tiddler_construct_DefaultValues() {
        Tiddler t = new Tiddler();
        assertNotNull(t.getTitle());
        assertNotNull(t.getCreateDate());
        assertNotNull(t.getModifier());
        assertNotNull(t.getCreator());
    }

    @Test
    public void Tiddler_construct_Title() {
        Tiddler t = new Tiddler("Test");
        assertEquals("Test", t.getTitle());
        assertNotNull(t.getCreateDate());
        assertNotNull(t.getModifier());
        assertNotNull(t.getCreator());
    }

    @Test
    public void Tiddler_parent_NoParentAvailable() {
        Tiddler t = new Tiddler();
        assertNull(t.getParent());
    }

    @Test
    public void Tiddler_parent_ParentAvailable() {
        Tiddler t1 = new Tiddler("ParentTiddler");
        Tiddler t2 = new Tiddler();
        t2.addTiddler(t1);

        assertNotNull(t1.getParent());
        assertEquals(t2, t1.getParent());
    }

    @Test
    public void Tiddler_parent_ModifyParentOutside() {
        Tiddler t1 = new Tiddler("ParentTiddler");
        Tiddler t2 = new Tiddler();
        t2.addTiddler(t1);

        Tiddler parent = t1.getParent();
        assertEquals(t2, parent);

        parent.setText("Changed Parent Title");
        assertEquals(parent.getTitle(), t2.getTitle());
    }

    @Test
    public void Tiddler_title_ModifyOutside() {
        Tiddler t = new Tiddler("Test");
        String title = t.getTitle();
        assertEquals("Test", title);
        title = "TestChanged";
        assertNotEquals(title, t.getTitle());
    }

    @Test
    public void Tiddler_title_ModifySpecialTiddler() {
        Tiddler t = Tiddler.createMainMenuTiddler();
        assertEquals("MainMenu", t.getTitle());
        try {
            t.setTitle("New one");
            assertTrue("IllegalArgumentException was expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }

    }

    @Test
    public void Tiddler_addTiddler_InvalidArgument() {
        Tiddler t = new Tiddler();
        try {
            t.addTiddler(null);
            assertTrue("IllegalArgumentException is expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void Tiddler_addTiddler_NewTiddler() {
        Tiddler t = new Tiddler("t");
        assertEquals(0, t.listTiddlers().size());

        Tiddler t2 = new Tiddler("t2");
        t.addTiddler(t2);

        assertEquals(1, t.listTiddlers().size());
        assertEquals(t2, t.listTiddlers().get(0));

        Tiddler t3 = new Tiddler("t3");
        t.addTiddler(t3);
        assertEquals(2, t.listTiddlers().size());
        assertEquals(t3, t.listTiddlers().get(1));
    }

    @Test
    public void Tiddler_listTiddler_ModifyList() {
        Tiddler t = new Tiddler("t");
        Tiddler t2 = new Tiddler("t2");
        t.addTiddler(t2);
        Tiddler t3 = new Tiddler("t3");
        t.addTiddler(t3);
        try {
            List<Tiddler> tiddlers = t.listTiddlers();
            assertEquals(2, tiddlers.size());
            tiddlers.remove(0);
            assertEquals(2, t.listTiddlers().size());
        } catch (UnsupportedOperationException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void Tiddler_createDate_ModifyOutside() {
        Tiddler t = new Tiddler();
        Date actualCreateDate = t.getCreateDate();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            // can be ignored!
        }
        actualCreateDate = new Date();
        assertNotEquals(actualCreateDate, t.getCreateDate());
    }

    @Test
    public void Tiddler_uid_ModifyOutside() {
        Tiddler t = new Tiddler();
        UID actualUID = t.getId();
        actualUID = new UID();
        assertNotEquals(actualUID, t.getId());
    }

    @Test
    public void Tiddler_addTag_InvalidArgument() {
        Tiddler t = new Tiddler();
        try {
            t.addTag(null);
            assertTrue("IllegalArgumentException is expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void Tiddler_addTag_NewTiddler() {
        Tiddler t = new Tiddler("t");
        assertEquals(0, t.getTags().size());
        t.addTag("tag1");
        assertEquals(1, t.getTags().size());
        t.addTag("tag2");
        assertEquals(2, t.getTags().size());
    }

    @Test
    public void Tiddler_getTags_ModifyList() {
        Tiddler t = new Tiddler("t");
        t.addTag("tag1");
        t.addTag("tag2");
        try {
            List<String> tags = t.getTags();
            assertEquals(2, tags.size());
            tags.remove(0);
            assertEquals(2, t.getTags().size());
        } catch (UnsupportedOperationException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void Tiddler_text_ApppendText() {
        Tiddler t = new Tiddler();
        t.setText(null);
        t.appendText("text1");
        assertEquals("text1", t.getText());

        t.appendText("text2");
        assertEquals("text1text2", t.getText());
    }

    @Test
    public void Tiddler_text_setText() {
        Tiddler t = new Tiddler();
        t.setText("text1");
        assertEquals("text1", t.getText());
        t.setText(null);
        assertNull(t.getText());
    }

    @Test
    public void Tiddler_compare_TiddlerNotEquals() {
        Tiddler t1 = new Tiddler("text1");
        Tiddler t2 = new Tiddler("text1");
        assertNotEquals(t1, t2);

    }

}
