package de.bimalo.common;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p> A "manager" providing localization support for numbers, resources and
 * dates.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class Localizer {

    /**
     * Logger for this class.
     */
    final Logger logger = LoggerFactory.getLogger(Localizer.class);
    /**
     * The default Locale used when no special Locale is used.
     */
    private static final Locale DEFAULT_LOCALE = Locale.GERMANY;
    /**
     * The name of the default ResourceBundle.
     */
    private static final String DEFAULT_BUNDLE =
            "de.bimalo.common.resources.CommonsResources";
    /**
     * The default pattern to format a java.util.Date object.
     */
    private static final String DEFAULT_DATE_PATTERN = "dd.MM.yyyy";
    /**
     * The configured Locale of this Localizer.
     */
    private Locale locale = null;
    /**
     * A Map containing the "registered" ResourceBundles. The Localizer can
     * handle multiple ResourceBundles.
     */
    private Map resourceBundleRegistry = Collections.synchronizedMap(new HashMap());

    /**
     * Constructs a new
     * <code>Localizer</code> with the default Locale.
     *
     * @exception MissingResourceException if the default ResourceBundle could
     * not be loaded.
     */
    public Localizer() {
        locale = DEFAULT_LOCALE;
        initializeDefaultBundle();
        logger.trace("Localizer with Locale {} initialized.", locale);
    }

    /**
     * Constructs a new
     * <code>Localizer</code> with the given Locale. If locale is null the
     * default Locale will be used instead.
     *
     * @param locale the Locale (can be null)
     * @exception MissingResourceException if the default ResourceBundle could
     * not be loaded.
     */
    public Localizer(Locale locale) {
        this.locale = ( locale == null ? DEFAULT_LOCALE : locale );
        initializeDefaultBundle();
        logger.trace("Localizer with Locale {} initialized.", locale);
    }

    /**
     * <p> Re-configures the Localizer with a new
     * <code>Locale</code>.</p> <p> If local is null this function does nothing.
     * Otherweise all registered ResourceBundles will be reloaded with the new
     * Locale. Furthermore if
     * <code>theReferrer</code> is not null it tries to use the class loader of
     * this object to reload all registered ResourceBundles (intended to use
     * within a web application!).
     *
     * @param locale the new Locale, if null does nothing!
     * @param theReferrer use the class loader of thi class to load the
     * ResourceBundles
     * @exception MissingResourceException if ResourceBundles could not be
     * loaded
     */
    public void setLocale(Locale locale, Class theReferrer) {
        if (locale == null || locale.equals(this.locale)) {
            return;
        }
        this.locale = locale;
        Set<String> keys = resourceBundleRegistry.keySet();
        for (String key : keys) {
            ResourceBundle resourceBundle = loadResourceBundle(key, theReferrer);
            resourceBundleRegistry.put(key, resourceBundle);
        }
    }

    /**
     * Re-configures this Localizer with a new
     * <code>Locale</code>. It calls
     * <code>setLocale(locale, null)</code>.
     *
     * @param locale the new Locale, if null does nothing!
     * @exception MissingResourceException if ResourceBundles could not be
     * loaded
     */
    public void setLocale(Locale locale) {
        setLocale(locale, null);
    }

    /**
     * Gets the Locale used by this Localizer.
     *
     * @return the Locale object
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Lookup a
     * <code>ResourceBundle</code> with a given name in the registry of this
     * Localizer. The name should be identical to that one used to add the
     * ResourceBundle, like
     * <code>org.test.resources.ExtendedResources</code>. A context sensitiv
     * search will be performed.
     *
     * @param bundleName the name of the ResourceBundle.
     * @return The loaded ResourceBundle or null if not found.
     * @exception IllegalArgumentException if bundleName is null or empty
     */
    public ResourceBundle getResourceBundle(String bundleName) {
        Assert.notNull(bundleName);
        ResourceBundle resourceBundle = (ResourceBundle) resourceBundleRegistry.get(bundleName);
        return resourceBundle;
    }

    /**
     * Adds a
     * <code>ResourceBundle</code> with the given name to this Localizer object.
     * It tries to use the class loader from the given class object
     * (theReferrer) to locate the ResourceBundle. This function is intended to
     * use within a web application. If a ResourceBundle with the given name
     * could not be loaded an exception will be thrown. Furthermore if a
     * ResourceBundle with the given name already exists nothing will be
     * overwritten!
     *
     * @param bundleName the name of the new ResourceBundle, e.g.
     * "org.test.resources.ExtendedResources"
     * @param theReferrer the class uses this Localizer to add a ResourceBundle
     * @exception MissingResourceException if the ResourceBundle could not be
     * found in the class path
     * @exception IllegalArgumentException if bundleName is null or length == 0
     */
    public void addResourceBundle(String bundleName, Class theReferrer) {
        Assert.notNull(bundleName);
        ResourceBundle resourceBundle = loadResourceBundle(bundleName, theReferrer);
        resourceBundleRegistry.put(bundleName, resourceBundle);
    }

    /**
     * Lookup all registered ResourceBundles for a value with the given key. The
     * first occurrence of the key in a ResourceBundle will be used! If no value
     * has been found in any ResourceBundle the key itself is returned.
     *
     * @param key key used to search in all ResourceBundles
     * @return the found value in any ResourceBundle or the key itself if no
     * value found
     * @exception IllegalArgumentException if key is null or length == 0
     */
    public String getResourceString(String key) {
        Assert.notNull(key);
        return getResourceString(key, null);
    }

    /**
     * Lookup all registered ResourceBundles for a value with the given key. The
     * first occurrence of the key in a ResourceBundle will be used! If no value
     * has been found in any ResourceBundle the key itself is returned.
     *
     * @param key key used to search in all ResourceBundles
     * @param parameters an Array with values that could be used to customize
     * the value identified by the key with a message formatter (optional!)
     * @return the found value in any ResourceBundle or the key itself if no
     * value found
     * @exception IllegalArgumentException if key is null or length == 0
     */
    public String getResourceString(String key, Object[] parameters) {
        Assert.notNull(key);

        // Find value for key
        String value = findKeyInResourceBundles(key);
        if (value == null || value.isEmpty()) {
            return key;
        }

        // Process parameters
        int size = ( parameters != null ) ? parameters.length : 0;
        if (size > 0) {
            String[] stringParameters = new String[size];
            for (int i = 0; i < size; i++) {
                if (parameters[i] == null) {
                    continue;
                }
                String parameterValue = findKeyInResourceBundles(parameters[i].toString());
                stringParameters[i] = parameterValue != null ? parameterValue : parameters[i].toString();
            }

            // Incorporate parameters into value
            MessageFormat formatter = new MessageFormat(value);
            value = formatter.format(stringParameters);
        }

        return value;
    }

    /**
     * Converts the given Date object into a String using the given pattern. If
     * the pattern is null the default pattern "dd.MM.YYYY" is used.
     *
     * @param date the date to convert
     * @param pattern defines the pattern to convert the Date object as
     * described in <code>java.text.SimpleDateFormat</code>.
     * @return the formatted Date object as String.
     * @see java.text.SimpleDateFormat
     * @exception IllegalArgumentException if date is null
     */
    public String formatDateObject(Date date, String pattern) {
        Assert.notNull(date);

        String formattedDate = null;
        String applyPattern = ( pattern == null || pattern.isEmpty() ? DEFAULT_DATE_PATTERN : pattern );

        DateFormat df =
                DateFormat.getDateInstance(DateFormat.SHORT, locale);
        df.setLenient(false);

        if (df instanceof SimpleDateFormat) {
            SimpleDateFormat sdf = (SimpleDateFormat) df;
            sdf.applyPattern(applyPattern);
            formattedDate = sdf.format(date);
        } else {
            formattedDate = df.format(date);
        }

        return formattedDate;
    }

    /**
     * Converts the given Date object into a String. It uses the pattern defined
     * by
     * <code>java.text.DateFormat.MEDIUM</code>.
     *
     * @param date the date to convert
     * @return the formatted Date object as string.
     * @see java.text.DateFormat
     * @exception IllegalArgumentException if date is null
     */
    public String formatDateObject(Date date) {
        Assert.notNull(date);

        DateFormat df =
                DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        df.setLenient(false);
        String formattedDate = df.format(date);

        return formattedDate;
    }

    /**
     * Converts a given Double value into a String. With the parameters
     * <code>maxIntegerDigits and maxFractionDigits</code> the layout of the
     * String can be customized.
     *
     * @param number the Double object to format
     * @param maxIntegerDigits max. number of integer digits (
     * @param maxFractionDigits max. number of fraction digits
     * @return the formatted String
     * @exception IllegalArgumentException if number is null or maxIntegerDigits
     * < 0 or maxFractionDigits < 0
     */
    public String formatNumberObject(Double number, int maxIntegerDigits,
            int maxFractionDigits) {
        Assert.notNull(number);
        Assert.isTrue(maxIntegerDigits >= 0, "A negative maxIntegerDigits is not valid.");
        Assert.isTrue(maxFractionDigits >= 0, "A negative maxFractionDigits is not valid.");

        String formattedNumber = null;
        NumberFormat nf = NumberFormat.getInstance(locale);
        nf.setMinimumIntegerDigits(maxIntegerDigits);
        nf.setMinimumFractionDigits(maxFractionDigits);
        nf.setMaximumFractionDigits(maxFractionDigits);

        if (nf instanceof DecimalFormat) {
            DecimalFormat dcf = (DecimalFormat) nf;
            formattedNumber = dcf.format(number.doubleValue());
        } else {
            formattedNumber = nf.format(number.doubleValue());
        }

        return formattedNumber;
    }

    /**
     * Parses a given String and tries to create a Date object with the
     * collected data. It tries to parse the String with pre-defined formats in
     * <code>java.text.DateFormat.</code>. The formats SHORT, MEDIUM, LONG, FULL
     * are used!. If the String could not be converted into a Date object an
     * exception raised!
     *
     * @param dateString a String contains date and time information
     * @return a Date object
     * @exception ParseException if the String could not be parsed
     * @see java.text.DateFormat
     * @exception IllegalArgumentException if dateString is null or length == 0
     */
    public Date parseDateString(String dateString) throws ParseException {
        Assert.notNull(dateString);

        Date date = null;
        try {
            date = parseDateString(dateString, DateFormat.SHORT, DateFormat.SHORT);
        }
        catch (ParseException ex1) {
            try {
                date = parseDateString(dateString, DateFormat.MEDIUM, DateFormat.MEDIUM);
            }
            catch (ParseException ex2) {
                try {
                    date = parseDateString(dateString, DateFormat.LONG, DateFormat.LONG);
                }
                catch (ParseException ex3) {
                    try {
                        date = parseDateString(dateString, DateFormat.FULL, DateFormat.FULL);
                    }
                    catch (ParseException ex4) {
                        date = parseDateString(dateString, DateFormat.MEDIUM, DateFormat.LONG);
                    }
                }
            }
        }

        return date;
    }

    /**
     * Parses a given String and tries to create a Date object with the
     * collected data. First it uses the given pattern to parse the String. If
     * the pattern could not be applied It tries to parse the String with
     * pre-defined formatsin
     * <code>java.text.DateFormat.</code>. The formats SHORT, MEDIUM, LONG, FULL
     * are used!. If the String could not be converted into a Date object an
     * exception raised!
     *
     * @param dateString a String contains date and time information
     * @param pattern a String describing the format of the Date, like
     * "dd.MM.YYYY"
     * @return a Date object
     * @exception ParseException if the String could not be parsed
     * @see java.text.DateFormat
     * @exception IllegalArgumentException if dateString is null or length == 0
     */
    public Date parseDateString(String dateString,
            String pattern) throws ParseException {
        Assert.notNull(dateString);

        Date date = null;
        String applyPattern = ( pattern == null || pattern.isEmpty() ? DEFAULT_DATE_PATTERN : pattern );

        try {
            DateFormat df =
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
                    locale);
            df.setLenient(false);
            if (df instanceof SimpleDateFormat) {
                SimpleDateFormat sdf = (SimpleDateFormat) df;
                sdf.applyPattern(applyPattern);
                date = sdf.parse(dateString);
            }
        }
        catch (ParseException ex) {
            date = parseDateString(dateString);
        }
        return date;
    }

    /**
     * Parses a given String and converts it into a Number.
     *
     * @param numberString the String to convert into a Number value
     * @return a Number object
     * @exception ParseException if the String could not be parsed
     * @see java.text.NumberFormat
     * @exception IllegalArgumentException if numberString is null or length ==
     * 0
     */
    public Number parseNumberString(String numberString) throws ParseException {
        Assert.notNull(numberString);

        NumberFormat nf = NumberFormat.getInstance(locale);
        Number num = nf.parse(numberString);
        return num;
    }

    /**
     * Removes the
     * <code>ResourceBundle</code> with the given name if exists.
     *
     * @param bundleName the name of the new ResourceBundle, e.g.
     * "org.test.resources.ExtendedResources"
     */
    public void removeResourceBundle(String bundleName) {
        if (bundleName == null || bundleName.equals(DEFAULT_BUNDLE)) {
            return;
        }

        resourceBundleRegistry.remove(bundleName);
    }

    /**
     * Converts a given String (dateString) into a Data object using DateFormat
     * with the given date and time formatting style.
     *
     * @param dateString the String to convert to Date
     * @param dateStyle the given date formatting style.
     * @param timeStyle the given time formatting style.
     * @return the converted Date object
     * @exception ParseException if conversion failed
     */
    private Date parseDateString(String dateString, int dateStyle, int timeStyle) throws ParseException {
        DateFormat df = DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale);
        df.setLenient(false);
        Date date = df.parse(dateString);
        return date;
    }

    /**
     * Loads the
     * <code>ResourceBundle</code> with the given name from the class path. If
     * it can not locate the bundle with the current class loader or with the
     * class loader from the second parameter
     * <code>theRefererer</code> it uses the context class loader.
     *
     * @param bundleName the name of the ResourceBundle, like
     * "org.test.resources.CommonsResources"
     * @param theReferrer the class uses this Localizer to locate the bundle
     * @return the loaded ResourceBundle object
     * @exception MissingResourceException if the ResourceBundle could not be
     * loaded
     * @exception IllegalArgumentException if bundleName is null or length == 0
     */
    private ResourceBundle loadResourceBundle(String bundleName,
            Class theReferrer) {
        Assert.notNull(bundleName);
        ResourceBundle bundle;
        try {
            if (theReferrer == null || theReferrer.getClassLoader() == null) {
                bundle = ResourceBundle.getBundle(bundleName, locale);
            } else {
                bundle =
                        ResourceBundle.getBundle(bundleName, locale, theReferrer.getClassLoader());
            }
        }
        catch (MissingResourceException ex) {
            bundle =
                    ResourceBundle.getBundle(bundleName, locale, Thread.currentThread().getContextClassLoader());
        }

        return bundle;
    }

    /**
     * Iterates through all registered ResourceBundles and look for the
     * existence of the given key. If a value has been found with the given key
     * this value will be returned otherwise the key itself.
     *
     * @param key the key in the ResourceBundle
     * @return the found value in the ResourceBundle or the key itself
     */
    private String findKeyInResourceBundles(String key) {
        String value = null;

        Collection<ResourceBundle> resourceBundles = resourceBundleRegistry.values();
        for (ResourceBundle bundle : resourceBundles) {
            try {
                value = bundle.getString(key);
                if (value != null) {
                    break;
                }
            }
            catch (Exception ex) {
                value = null;
            }
        }

        return ( value != null ? value : key );
    }

    /**
     * Initializes the default ResourceBundle.
     *
     * @exception MissingResourceException if operation fails
     */
    private void initializeDefaultBundle() {
        if (resourceBundleRegistry == null) {
            resourceBundleRegistry = Collections.synchronizedMap(new HashMap());
        }

        ResourceBundle defaultBundle = loadResourceBundle(DEFAULT_BUNDLE, getClass());
        resourceBundleRegistry.put(DEFAULT_BUNDLE, defaultBundle);
    }
}
