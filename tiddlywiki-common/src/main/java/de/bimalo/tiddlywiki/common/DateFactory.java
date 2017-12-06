package de.bimalo.tiddlywiki.common;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A factory to create objects of type <code>java.util.Date</code>.
 * <p>
 * It is implemented as singleton!
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 * @see java.util.Date
 */
public final class DateFactory {

    /**
     * The "singleton" DateFactory instance.
     */
    private static DateFactory factory = null;
    /**
     * The Locale used to create Date objects considering language specific
     * formats.
     */
    private Locale locale = null;

    /**
     * Constructs a new <code>DateFactory</code> object. When local is null the
     * default Locale will be used instead.
     *
     * @param locale the Locale (can be null)
     */
    private DateFactory(Locale locale) {
        if (locale == null) {
            this.locale = Locale.getDefault();
        } else {
            this.locale = locale;
        }
    }

    /**
     * Returns a <code>DateFactory</code> using the default Locale.
     *
     * @return the initialized DateFactory.
     */
    public static synchronized DateFactory getInstance() {
        if (factory == null) {
            factory = new DateFactory(null);
        }
        return factory;
    }

    /**
     * Returns a <code>DateFactory</code> using the given Locale.
     *
     * @param locale the Locale object
     * @return the initialized DateFactory.
     * @exception IllegalArgumentException if DateFactory not yet initialized
     * and locale is null
     */
    public static synchronized DateFactory getInstance(Locale locale) {
        if (factory == null) {
            Assert.notNull(locale);
            factory = new DateFactory(locale);
        }
        return factory;
    }

    /**
     * Returns a <code>DateFactory</code> using the given Localizer.
     *
     * @param localizer a manager for language specific settings
     * @return the initialized DateFactory.
     * @exception IllegalArgumentException if DateFactory not yet initialized
     * and localizer is null
     */
    public static synchronized DateFactory getInstance(Localizer localizer) {
        if (factory == null) {
            Assert.notNull(localizer);
            factory = new DateFactory((localizer != null ? localizer.getLocale() : null));
        }
        return factory;
    }

    /**
     * <p>
     * Creates a <code>Date</code> object only with the day, month and year. It
     * does not include time settings!
     * </p>
     * Example:<br>
     * <code>
     *   Date date = DateFactory.getInstance().createDate(24,1,2004);
     * </code> Result = 24.1.2004
     *
     * @param day number between 1 and 31
     * @param month number between 1 and 12
     * @param year a 4 digit number, e.g. 1999
     * @return the created Date object.
     * @exception IllegalArgumentException if invalid parameters specified
     */
    public Date createDate(int day, int month, int year) {
        validateDateValues(day, month, year);

        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * <p>
     * Creates a <code>Date</code> object including time information.
     * </p>
     * Example:<br>
     * <code>
     *   Date date = DateFactory.getInstance().createDate(24,1,2004,15,24,30);
     * </code> Result = 24.1.2004 15:24:30
     *
     * @param day number between 1 and 31
     * @param month number between 1 and 12
     * @param year a 4 digit number, e.g. 1999
     * @param hour number between 0 and 23
     * @param minute number between 0 and 60
     * @param second number between 0 and 60
     * @return the created Date object.
     * @exception IllegalArgumentException if invalid parameters specified
     */
    public Date createDate(int day, int month, int year, int hour, int minute, int second) {
        validateDateValues(day, month, year);
        validateTimeValues(hour, minute, second);

        Calendar calendar = Calendar.getInstance(locale);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.SECOND, second);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Validates the input parameters day, month, year.
     *
     * @param day number between 1 and 31
     * @param month number between 1 and 12
     * @param year a 4 digit number, e.g. 1999
     */
    private void validateDateValues(int day, int month, int year) {
        Assert.isTrue(day >= 1 && day <= 31, "Day is not between 1 and 31.");
        Assert.isTrue(month >= 1 && month <= 12, "Month is not between 1 and 12.");
        Assert.isTrue(Integer.toString(year).length() == 4, "Year is not a 4 digit number.");
    }

    /**
     * Validates the input parameters day, month, year.
     *
     * @param hour number between 0 and 23
     * @param minute number between 0 and 60
     * @param second number between 0 and 60
     */
    private void validateTimeValues(int hour, int minute, int second) {
        Assert.isTrue(hour >= 0 && hour <= 23, "Hour is not between 0 and 23.");
        Assert.isTrue(minute >= 0 && minute <= 60, "Minute is not between 0 and 60.");
        Assert.isTrue(second >= 0 && second <= 60, "Second is not between 0 and 60.");
    }
}
