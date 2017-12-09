package de.bimalo.tiddlywiki.common;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * <p>
 * A test case for <code>CommandLineParser</code>.
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see CommandLineParser
 */
public class CommandLineParserTest {

    public CommandLineParserTest() {
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
    public void CommandLineParser_RegularExpression() {
        Pattern p = Pattern.compile("-[\\w]+=[\\w\\W]+");
        Matcher m = p.matcher("-test=/path/test2.txt");
        assertEquals(true, m.matches());
    }

    @Test
    public void CommandLineParser_parse_OneValidArgument() {
        String[] args = new String[]{"-rootFolder=/test/test11/xxx"};
        CommandLineParser parser = new CommandLineParser();
        parser.parseArguments(args);
        assertEquals("/test/test11/xxx", parser.getArgumentValue("rootFolder"));

    }

    @Test
    public void CommandLineParser_parse_TwoValidArgument() {
        String[] args = new String[]{"-rootFolder=/test/test11/xxx", "-rootFolder2=/test/test22/xxx"};
        CommandLineParser parser = new CommandLineParser();
        parser.parseArguments(args);
        assertEquals("/test/test11/xxx", parser.getArgumentValue("rootFolder"));
        assertEquals("/test/test22/xxx", parser.getArgumentValue("rootFolder2"));
    }

    @Test
    public void CommandLineParser_parse_ValidArgumentContainingWhitespace() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx",};
        CommandLineParser parser = new CommandLineParser();
        parser.parseArguments(args);
        assertEquals("/test/test11 muster/xxx", parser.getArgumentValue("rootFolder"));
    }

    @Test
    public void CommandLineParser_parse_ArgumentNotAvailable() {
        CommandLineParser parser = new CommandLineParser();
        assertNull(parser.getArgumentValue("test"));
    }

    @Test
    public void CommandLineParser_parse_PropertiesFileDoesNotExistsArgument() {
        String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx", "-configFile=/tmp/test.properties"};
        CommandLineParser parser = new CommandLineParser();
        parser.parseArguments(args);
        assertEquals("/test/test11 muster/xxx", parser.getArgumentValue("rootFolder"));
        assertEquals("/tmp/test.properties", parser.getArgumentValue("configFile"));

    }

    @Test
    public void CommandLineParser_parse_PropertiesFileArgument() {
        URL fileURL = this.getClass().getResource("/config.properties");

        try {
            File propertiesFile = new File(fileURL.toURI());
            String[] args = new String[]{"-rootFolder=/test/test11 muster/xxx", "-configFile=" + propertiesFile.getAbsolutePath()};
            CommandLineParser parser = new CommandLineParser();
            parser.parseArguments(args);
            assertEquals("value 2", parser.getArgumentValue("argument2"));
            assertEquals("/test/test11 muster/xxx", parser.getArgumentValue("rootFolder"));

        }
        catch (URISyntaxException ex) {
            assertTrue(ex.getMessage(), false);
        }

    }

    @Test
    public void CommandLineParser_parse_InvalidSyntaxInFirstArgument() {
        String[] args = new String[]{"rootFolder=/test/test11/xxx", "-rootFolder2=/test/test22/xxx"};
        CommandLineParser parser = new CommandLineParser();
        try {
            parser.parseArguments(args);
            assertTrue("InvalidArgumentException expected.", false);
        }
        catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }

    @Test
    public void CommandLineParser_parse_InvalidSyntaxInSecondArgument() {
        String[] args = new String[]{"-rootFolder=/test/test11/xxx", "rootFolder2=/test/test22/xxx"};
        CommandLineParser parser = new CommandLineParser();
        try {
            parser.parseArguments(args);
            assertTrue("InvalidArgumentException expected.", false);
        }
        catch (IllegalArgumentException ex) {
            assertTrue(true);
        }
    }
}
