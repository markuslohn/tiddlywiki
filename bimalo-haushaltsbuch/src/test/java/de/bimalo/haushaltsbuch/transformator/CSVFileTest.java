package de.bimalo.haushaltsbuch.transformator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.vfs2.FileNotFolderException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * Test case for class <code>CSVFile</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class CSVFileTest {

    public CSVFileTest() {
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
    public void CSVFileTest_Construct_NullArgument() {
        try {
            CSVFile file = new CSVFile(null);
        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    @Test
    public void CSVFileTest_Construct_EmptyStringArgument() {
        try {
            CSVFile file = new CSVFile("");
        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    @Test
    public void CSVFileTest_Construct_FileNotExists() {
        try {
            CSVFile file = new CSVFile("file:/xyc.csv");
            assertEquals(false, file.exists());
        } catch (Exception ex) {
            fail("An " + ex.getClass().getName() + " was not expected in this test.");
        }
    }

    @Test
    public void CSVFileTest_Construct_FileExists() {
        try {
            File testfile = resolveFile("/sparkasse1.csv");
            assertEquals(true, testfile.exists());

            CSVFile csvfile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(true, csvfile.exists());
        } catch (Exception ex) {
            fail("An " + ex.getClass().getName() + " was not expected in this test.");
        }
    }

    @Test
    public void CSVFileTest_Construct_NoFile() {
        try {
            File testfolder = resolveFile("/testfolder");
            assertEquals(true, testfolder.exists());

            CSVFile file = new CSVFile(testfolder.getAbsolutePath());
        } catch (Exception ex) {
            if (!(ex instanceof FileNotFolderException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    @Test
    public void CSVFileTest_getPath_ModifyPath() {
        try {
            File testfile = resolveFile("sparkasse1.csv");
            assertEquals(true, testfile.exists());

            CSVFile csvfile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(true, csvfile.exists());

            String path = csvfile.getPath();
            assertNotNull(path);
            assertEquals(testfile.getAbsolutePath(), path);

            path = path.replace("a", "b");
            String path2 = csvfile.getPath();
            assertEquals(testfile.getAbsolutePath(), path2);

        } catch (Exception ex) {
            fail("An " + ex.getClass().getName() + " was not expected in this test.");
        }
    }

    @Test
    public void CSVFileTest_delete_FileExists() {
        try {
            File testfile = createFile("testfolder", "file2delete.csv");
            assertEquals(true, testfile.exists());

            CSVFile csvFile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(true, csvFile.exists());

            csvFile.delete();
            assertEquals(false, csvFile.exists());

        } catch (Exception ex) {
            fail("An " + ex.getClass().getName() + " was not expected in this test.");
        }
    }

    @Test
    public void CSVFileTest_delete_NoFile() {
        try {
            File testfile = new File("FileNotExists.csv");

            CSVFile csvFile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(false, csvFile.exists());

            csvFile.delete();

        } catch (Exception ex) {
            fail("An " + ex.getClass().getName() + " was not expected in this test.");
        }
    }

    @Test
    public void CSVFileTest_movtTo_NullArgument() {
        try {
            File testfile = resolveFile("sparkasse1.csv");
            assertEquals(true, testfile.exists());

            CSVFile csvfile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(true, csvfile.exists());

            csvfile.moveTo(null);
        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    @Test
    public void CSVFileTest_moveTo_EmptyStringArgument() {
        try {
            File testfile = resolveFile("sparkasse1.csv");
            assertEquals(true, testfile.exists());

            CSVFile csvfile = new CSVFile(testfile.getAbsolutePath());
            assertEquals(true, csvfile.exists());

            csvfile.moveTo("");
        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    @Test
    public void CSVFileTest_moveTo_NormalOperation() {
        try {
            File sourceFile = createFile("/", "file2move.csv");
            assertEquals(true, sourceFile.exists());
            File targetFile = createFile("testfolder", "file2move.csv");
            assertEquals(true, targetFile.exists());

            CSVFile csvfile = new CSVFile(sourceFile.getAbsolutePath());
            assertEquals(true, csvfile.exists());
            csvfile.moveTo(targetFile.getAbsolutePath());

            String path = csvfile.getPath();
            File resultFile = resolveFile("/testfolder/file2move.csv");
            assertEquals(targetFile.getAbsolutePath(), path);

            csvfile.delete();

        } catch (Exception ex) {
            if (!(ex instanceof IllegalArgumentException)) {
                fail("An " + ex.getClass().getName() + " was not expected in this test.");
            }
        }
    }

    private File createFile(String folderName, String fileName) throws IOException {
        File parentFolder = resolveFile(folderName);
        File file = new File(parentFolder, fileName);
        boolean result = file.createNewFile();
        return file;
    }

    private File resolveFile(String name) {
        String path = null;
        if (!name.startsWith("/")) {
            StringBuilder sb = new StringBuilder();
            sb.append("/");
            sb.append(name);
            path = sb.toString();
        } else {
            path = name;
        }
        URL url = this.getClass().getResource(path);
        File file = new File(url.getFile());
        return file;
    }

    /*
     @Test
     public void firstTest() {
     try {
                 
     String pathname = "/home/oracle/Documents/bimalo/haushaltsbuch2/Transformations/20140825-43435114-umsatz.csv";
     CSVFile csvFile = new CSVFile(pathname);
     List records = csvFile.readRecords();
            
     String targetPathname = "/home/oracle/Documents/bimalo/haushaltsbuch2/Transformations/test.csv";
     CSVFile target = new CSVFile(targetPathname);
     target.writeRecords(null, records);
     System.out.println("Success");

     } catch (Exception ex) {
     ex.printStackTrace();
     }

     }
     */
}
