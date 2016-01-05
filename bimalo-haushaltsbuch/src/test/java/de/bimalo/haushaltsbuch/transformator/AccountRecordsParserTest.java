package de.bimalo.haushaltsbuch.transformator;

import de.bimalo.haushaltsbuch.transformator.delete.CSVFileParser;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * <p>
 * Test case for class <code>CSVFileParser</code>.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class AccountRecordsParserTest {

    public AccountRecordsParserTest() {
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

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void firstTest() {
        try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject inputFile = fsManager.resolveFile("/home/oracle/Documents/bimalo/haushaltsbuch2/Transformations/20140825-43435114-umsatz.csv");

            CSVFileParser parser = new CSVFileParser(inputFile, CSVFileTypes.SPARKASSE);
           // Haushaltsbuch book = parser.parse();
            System.out.println();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) {
        try {
            FileSystemManager fsManager = VFS.getManager();
            FileObject inputFile = fsManager.resolveFile("/home/oracle/Documents/bimalo/haushaltsbuch2/Transformations/20140825-43435114-umsatz.csv");

            CSVFileParser parser = new CSVFileParser(inputFile, CSVFileTypes.SPARKASSE);
            //Haushaltsbuch book = parser.parse();
            System.out.println();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
