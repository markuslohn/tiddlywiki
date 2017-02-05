package de.bimalo.tiddlywiki.generator;

import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.TiddlyWiki;
import de.bimalo.tiddlywiki.generator.DefaultTiddlyWikiSerializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import org.apache.commons.vfs2.FileContent;
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
 * A test case for <code>DefaultTiddlyWikiSerializer</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see DefaultTiddlyWikiSerializer
 */
public class DefaultTiddlyWikiSerializerTest {

    public DefaultTiddlyWikiSerializerTest() {
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
    public void DefaultTiddlyWikiSerializer_construct_InvalidArguments() {
        try {
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(null, null);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }

        try {
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(new TiddlyWiki(), null);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }

        try {
            FileObject templateFile = getFileObject("test.html", "/tmp/test", FileType.FILE, true);
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(null, templateFile);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }
    }

    @Test
    public void DefaultTiddlyWikiSerializer_construct_TemplateFileExistsAndIsADocument() {
        try {
            FileObject templateFile = getFileObject("test.html", "/tmp/test", FileType.FILE, true);
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(new TiddlyWiki(), templateFile);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }
    }

    @Test
    public void DefaultTiddlyWikiSerializer_construct_TemplateFileExistsAndIsAFolder() {
        try {
            FileObject templateFile = getFileObject("test.html", "/tmp/test", FileType.FOLDER, true);
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(new TiddlyWiki(), templateFile);
            assertTrue("FileNotFolderException expected.", false);
        } catch (FileNotFolderException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }
    }

    @Test
    public void DefaultTiddlyWikiSerializer_construct_TemplateFileDoesNotExist() {
        try {
            FileObject templateFile = getFileObject("test.html", "/tmp/test", FileType.FILE, false);
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(new TiddlyWiki(), templateFile);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }
    }

    @Test
    public void DefaultTiddlyWikiSerializer_writeObject_InvalidArgument() {
        try {
            FileObject templateFile = getFileObject("test.html", "/tmp/test", FileType.FILE, true);
            DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(new TiddlyWiki(), templateFile);
            serializer.writeObject(null);
            assertTrue("IllegalArgumentException expected.", false);
        } catch (IllegalArgumentException ex) {
            assertTrue(true);
        } catch (Exception ex) {
            assertTrue(ex.getClass().getName() + " not expected.", false);
        }
    }

    @Test
    public void DefaultTiddlyWikiSerializer_writeObject_TiddlyWiki() {
        try {
            TiddlyWiki tw = getTiddlyWiki();
            FileObject templateFile = getDocumentFileObject("test.html", "/tmp/test", getTemplateFileContent());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(tw, templateFile);
                serializer.writeObject(oos);
                System.out.println(baos.toString());

            } finally {
                baos.close();
                oos.close();
            }
        } catch (Exception ex) {
            assertTrue(ex.getMessage(), false);
        }

    }

    private String getTemplateFileContent() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n");
        sb.append("<body>\n");
        sb.append("<div id=\"storeArea\">");
        sb.append("</div id=\"storeArea\">");
        sb.append("</body>\n");
        sb.append("/<html>\n");

        return sb.toString();
    }

    private TiddlyWiki getTiddlyWiki() {
        TiddlyWiki tw = new TiddlyWiki();
        tw.setTitle("Test TiddlyWiki");

        Tiddler t1 = new Tiddler("t1");
        t1.setText("Test t1");
        tw.addTiddler(t1);

        Tiddler t2 = new Tiddler("t2");
        t1.setText("Test t2");
        tw.addTiddler(t2);

        return tw;
    }

    private FileObject getDocumentFileObject(String name, String path, String text) throws FileSystemException {
        FileContent documentContent = mock(FileContent.class);
        when(documentContent.getInputStream()).thenReturn(new ByteArrayInputStream(text.getBytes()));
        FileName documentName = mock(FileName.class);
        when(documentName.getPath()).thenReturn(path + "/" + name);
        FileObject document = mock(FileObject.class);
        when(document.getName()).thenReturn(documentName);
        when(document.getContent()).thenReturn(documentContent);
        when(document.getType()).thenReturn(FileType.FILE);
        when(document.exists()).thenReturn(true);

        return document;
    }

    private FileObject getFileObject(String name, String path, FileType type, boolean exists) throws FileSystemException {
        FileName fileName = mock(FileName.class);
        when(fileName.getBaseName()).thenReturn(name);
        when(fileName.getPath()).thenReturn(path + "/" + name);

        FileObject file = mock(FileObject.class);
        when(file.exists()).thenReturn(exists);
        when(file.getType()).thenReturn(type);
        when(file.getName()).thenReturn(fileName);

        return file;
    }

}
