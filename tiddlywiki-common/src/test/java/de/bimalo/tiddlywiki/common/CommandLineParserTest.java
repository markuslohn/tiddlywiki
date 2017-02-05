package de.bimalo.tiddlywiki.common;

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

  public CommandLineParserTest() {}

  @BeforeClass
  public static void setUpClass() {}

  @AfterClass
  public static void tearDownClass() {}

  @Before
  public void setUp() {}

  @After
  public void tearDown() {}

  @Test
  public void CommandLineParser_RegularExpression() {
    Pattern p = Pattern.compile("-[\\w]+=[\\w\\W]+");
    Matcher m = p.matcher("-test=/path/test2.txt");
    assertEquals(true, m.matches());
  }

  @Test
  public void CommandLineParser_parse_OneValidArgument() {
    String[] args = new String[] {"-rootFolder=/test/test11/xxx"};
    CommandLineParser parser = new CommandLineParser();
    parser.parseArguments(args);
    assertEquals("/test/test11/xxx", parser.getArgumentValue("rootFolder"));

  }

  @Test
  public void CommandLineParser_parse_TwoValidArgument() {
    String[] args = new String[] {"-rootFolder=/test/test11/xxx", "-rootFolder2=/test/test22/xxx"};
    CommandLineParser parser = new CommandLineParser();
    parser.parseArguments(args);
    assertEquals("/test/test11/xxx", parser.getArgumentValue("rootFolder"));
    assertEquals("/test/test22/xxx", parser.getArgumentValue("rootFolder2"));
  }

  @Test
  public void CommandLineParser_parse_ValidArgumentContainingWhitespace() {
    String[] args = new String[] {"-rootFolder=/test/test11 muster/xxx",};
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
  public void CommandLineParser_parse_InvalidSyntaxInFirstArgument() {
    String[] args = new String[] {"rootFolder=/test/test11/xxx", "-rootFolder2=/test/test22/xxx"};
    CommandLineParser parser = new CommandLineParser();
    try {
      parser.parseArguments(args);
      assertTrue("InvalidArgumentException expected.", false);
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    }
  }

  @Test
  public void CommandLineParser_parse_InvalidSyntaxInSecondArgument() {
    String[] args = new String[] {"-rootFolder=/test/test11/xxx", "rootFolder2=/test/test22/xxx"};
    CommandLineParser parser = new CommandLineParser();
    try {
      parser.parseArguments(args);
      assertTrue("InvalidArgumentException expected.", false);
    } catch (IllegalArgumentException ex) {
      assertTrue(true);
    }
  }
}
