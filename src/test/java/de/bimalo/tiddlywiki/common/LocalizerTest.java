package de.bimalo.tiddlywiki.common;

import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import junit.framework.TestCase;

/**
 * A unit test for <code>Localizer</code>.
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 */
public class LocalizerTest extends TestCase {

  private Locale deLocale = Locale.GERMAN;
  private Locale enLocale = Locale.ENGLISH;
  private Localizer instance = null;

  public LocalizerTest(String testName) {
    super(testName);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    instance = new Localizer(deLocale);
    instance.addResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle1",
        getClass());
    instance.addResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle2",
        getClass());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    instance = null;
  }

  /**
   * <p>
   * Test the feature of managing a ResourceBundle with class <code>Localizer</code>. It shows how
   * to add initialize a Localizer, how to add a ResourceBundle and get values back from added
   * ResourceBundles with the Localizer.
   * </p>
   * <p>
   * It does not consider any exception testing!
   * </p>
   */
  public void testResourceBundleManagement() {
    System.out.println("testResourceBundleManagement");

    // test with "german" language Localizer.
    assertEquals(deLocale, instance.getLocale());
    assertEquals("Wert1", instance.getResourceString("s1"));
    assertEquals("Wert2", instance.getResourceString("s2"));
    Object[] parameters = new Object[2];
    parameters[0] = "s1";
    parameters[1] = "s2";
    assertEquals("Test mit Wert1, Wert2 Werten.", instance.getResourceString("s4", parameters));
    ResourceBundle bundle1 =
        instance.getResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle1");
    assertNotNull(bundle1);
    assertEquals("Wert2", bundle1.getString("s2"));

    // test with "englisch" language Localizer (switch Locale)
    instance.setLocale(enLocale, getClass());
    assertEquals(enLocale, instance.getLocale());
    assertEquals("Value1", instance.getResourceString("s1"));
    assertEquals("Value2", instance.getResourceString("s2"));
    assertEquals("Test with Value1, Value2 values.", instance.getResourceString("s4", parameters));
    bundle1 =
        instance.getResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle1");
    assertNotNull(bundle1);
    assertEquals("Value2", bundle1.getString("s2"));
  }

  /**
   * Test of setLocale method, of class Localizer.
   */
  public void testSetLocale_Locale_Class() {
    System.out.println("setLocale");
    // A null Locale does not change the Locale in Localizer
    instance.setLocale(null, getClass());
    assertEquals(deLocale, instance.getLocale());

    // The same Locale as already set does not change the Localizer
    instance.setLocale(deLocale, getClass());
    assertEquals(deLocale, instance.getLocale());
  }

  /**
   * Test of setLocale method, of class Localizer.
   */
  public void testSetLocale_Locale() {
    System.out.println("setLocale");
    // A null Locale does not change the Locale in Localizer
    instance.setLocale(null);
    assertEquals(deLocale, instance.getLocale());

    // The same Locale as already set does not change the Localizer
    instance.setLocale(deLocale);
    assertEquals(deLocale, instance.getLocale());
  }

  /**
   * Test of getResourceBundle method, of class Localizer.
   */
  public void testGetResourceBundle() {
    System.out.println("getResourceBundle");

    try {
      instance.getResourceBundle(null);
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
    try {
      instance.getResourceBundle("");
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
  }

  /**
   * Test getResourceBundle by providing a name for the ResourceBundle that does not exist in
   * Localizer.
   */
  public void testGetNotAddedResourceBundle() {
    System.out.println("testGetNotAddedResourceBundle");
    ResourceBundle bundle1 = instance.getResourceBundle("NotExistingResourceBundle");
    assertNull(bundle1);
  }

  /**
   * Test of addResourceBundle method, of class Localizer.
   */
  public void testAddResourceBundle() {
    System.out.println("addResourceBundle");
    try {
      instance.addResourceBundle(null, getClass());
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
    try {
      instance.addResourceBundle("", getClass());
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
    try {
      instance.addResourceBundle("NotExistingResourceBundle", getClass());
      assertTrue("A MissingResourceException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof MissingResourceException);
    }
  }

  /**
   * Test of getResourceString method, of class Localizer.
   */
  public void testGetResourceString_String() {
    System.out.println("getResourceString");

    try {
      instance.getResourceString(null);
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
    try {
      instance.getResourceString("");
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
  }

  /**
   * Test getResourceString with a key that does not exist in the ResourceBundle. In this case the
   * key itself will be return as result.
   */
  public void testGetNotExistingResourceString_String() {
    System.out.println("testGetNotExistingResourceString_String");
    assertEquals("NotExistingKeyInBundle", instance.getResourceString("NotExistingKeyInBundle"));
  }

  /**
   * Test of getResourceString method, of class Localizer.
   */
  public void testGetResourceString_String_ObjectArr() {
    System.out.println("testGetResourceString_String_ObjectArr");
    Object[] parameters = new Object[2];
    parameters[0] = "s1";
    parameters[1] = "s2";

    try {
      instance.getResourceString(null, parameters);
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
    try {
      instance.getResourceString("", parameters);
      assertTrue("A IllegalArgumentException was expected.", false);
    } catch (Exception ex) {
      assertTrue(ex instanceof IllegalArgumentException);
    }
  }

  /**
   * Test different settings for the parameter array when using function getResourceString.
   */
  public void testGetResourceString_String_ObjectArr2() {
    System.out.println("testGetResourceString_String_ObjectArr2");

    assertEquals("Test mit {0}, {1} Werten.", instance.getResourceString("s4", null));
    assertEquals("Test mit {0}, {1} Werten.", instance.getResourceString("s4", new Object[0]));

    Object[] param1 = new Object[2];
    param1[0] = "DoesNotExistInBundle1";
    param1[1] = "DoesNotExistInBundle2";
    assertEquals("Test mit DoesNotExistInBundle1, DoesNotExistInBundle2 Werten.",
        instance.getResourceString("s4", param1));

    Object[] param2 = new Object[2];
    param2[0] = "s1";
    param2[1] = "DoesNotExistInBundle2";
    assertEquals("Test mit Wert1, DoesNotExistInBundle2 Werten.",
        instance.getResourceString("s4", param2));

    Object[] param3 = new Object[2];
    param3[0] = "DoesNotExistInBundle1";
    param3[1] = "s2";
    assertEquals("Test mit DoesNotExistInBundle1, Wert2 Werten.",
        instance.getResourceString("s4", param3));
  }

  /**
   * Test of formatDateObject method, of class Localizer.
   */
  public void testFormatDateObject_Date_String() {
    /*
     * System.out.println("formatDateObject"); Date date2convert =
     * DateFactory.getInstance(instance).createDate(2, 2, 2012); String result =
     * instance.formatDateObject(date2convert, "yy.MM.yyyy"); assertEquals("02.02.2012", result);
     */
  }

  /**
   * Test of formatNumberObject method, of class Localizer.
   */
  public void testFormatNumberObject() {
    /*
     * System.out.println("formatNumberObject"); Double number = Double.valueOf("47,1134"); String
     * result = instance.formatNumberObject(number, 3, 2); assertEquals("47,11", result);
     */
  }

  /**
   * Test of parseDateString method, of class Localizer.
   */
  public void testParseDateString_String() throws Exception {
    /*
     * System.out.println("parseDateString"); Date expResult =
     * DateFactory.getInstance(instance).createDate(2, 2, 2012); Date result =
     * instance.parseDateString("02.02.2012"); assertEquals(expResult, result);
     */
  }

  /**
   * Test of parseDateString method, of class Localizer.
   */
  public void testParseDateString_String_String() throws Exception {
    System.out.println("parseDateString");
    Date expResult = DateFactory.getInstance(instance).createDate(2, 2, 2012);
    Date result = instance.parseDateString("02.02.2012", "dd.MM.yyyy");
    assertEquals(expResult, result);
  }

  /**
   * Test of removeResourceBundle method, of class Localizer.
   */
  public void testRemoveResourceBundle() {
    System.out.println("removeResourceBundle");
    instance.removeResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle2");
    assertNull(
        instance.getResourceBundle("de.bimalo.tiddlywiki.common.resources.LocalizerTestBundle2"));
  }
}
