package de.bimalo.tiddlywiki.common;

import java.util.Collection;
import java.util.Map;

/**
 * <p>
 * Assists in validating arguments.
 * </p>
 *
 * <p>
 * The class is based along the lines of JUnit. If an argument value is deemed
 * invalid, an IllegalArgumentException is thrown. For example:
 * </p>
 *
 * <pre>
 * Assert.isTrue(i > 0, "The value must be greater than zero: ", i);
 * Assert.notNull(surname, "The surname must not be null");
 * </pre>
 *
 * <p>
 * For more Assert functions take a look at
 * <code>in org.apache.commons.lang.Validate</code> and/or
 * <code>org.springframework.util.Assert</code>.
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @since 1.0
 */
public final class Assert {

    /**
     * Private constructor for this utility class.
     */
    private Assert() {
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the test result is <code>false</code>.
     * </p>
     * <p>
     * This is used when validating according to an arbitrary boolean
     * expression, such as validating a primitive number or using your own
     * custom validation expression.
     * </p>
     *
     * <pre>
     * Assert.isTrue(myObject.isOk(), "The object is not OK: ", myObject);
     * </pre>
     *
     * @param expression a boolean expression
     * @param message the exception message you would like to see if the
     * expression is <code>false</code>
     * @exception IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the test result is <code>false</code>.
     * </p>
     * <p>
     * This is used when validating according to an arbitrary boolean
     * expression, such as validating a primitive number or using your own
     * custom validation expression.
     * </p>
     *
     * <pre>
     * Assert.isTrue(myObject.isOk(), "The object is not OK: ", myObject);
     * </pre>
     *
     * @param expression a boolean expression expression is <code>false</code>
     * @exception IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument is <code>null</code>.
     * </p>
     *
     * <pre>
     * Assert.notNull(myObject, "The object must not be null");
     * </pre>
     *
     * @param object the object to check is not <code>null</code>
     * @param message the exception message you would like to see if the object
     * is <code>null</code>
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
        if (object instanceof String && ((String) object).length() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument is <code>null</code>.
     * </p>
     *
     * <pre>
     * Assert.notNull(myObject, "The object must not be null");
     * </pre>
     *
     * @param object the object to check is not <code>null</code> if the object
     * is <code>null</code>
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument array is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myArray, "The array must not be empty");
     * </pre>
     *
     * @param array the array to check is not empty
     * @param message the exception message you would like to see if the array
     * is empty
     * @throws IllegalArgumentException if the array is empty
     */
    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument array is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myArray, "The array must not be empty");
     * </pre>
     *
     * @param array the array to check is not empty
     * @throws IllegalArgumentException if the array is empty
     */
    public static void notEmpty(Object[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("[Assertion failed] - the validated array is empty");
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument Collection is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myCollection, "The collection must not be empty");
     * </pre>
     *
     * @param collection the collection to check is not empty
     * @param message the exception message you would like to see if the
     * collection is empty
     * @throws IllegalArgumentException if the collection is empty
     */
    public static void notEmpty(Collection collection, String message) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument Collection is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myCollection, "The collection must not be empty");
     * </pre>
     *
     * @param collection the collection to check is not empty
     * @throws IllegalArgumentException if the collection is empty
     */
    public static void notEmpty(Collection collection) {
        if (collection == null || collection.size() == 0) {
            throw new IllegalArgumentException("[Assertion failed] - the validated collection is empty");
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument Map is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myMap, "The map must not be empty");
     * </pre>
     *
     * @param map the map to check is not empty
     * @param message the exception message you would like to see if the map is
     * empty
     * @throws IllegalArgumentException if the map is empty
     */
    public static void notEmpty(Map map, String message) {
        if (map == null || map.size() == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument Map is empty (<code>null</code> or no elements).
     * </p>
     *
     * <pre>
     * Assert.notEmpty(myMap, "The map must not be empty");
     * </pre>
     *
     * @param map the map to check is not empty
     * @throws IllegalArgumentException if the map is empty
     */
    public static void notEmpty(Map map) {
        if (map == null || map.size() == 0) {
            throw new IllegalArgumentException("[Assertion failed] - the validated map is empty");
        }
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument type is an instance of object.
     * </p>
     * .
     *
     * @param clazz the class object to check is instance of
     * @param obj the base object type
     */
    public static void isInstanceOf(Class clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    /**
     * <p>
     * Validate an argument, throwing <code>IllegalArgumentException</code> if
     * the argument type is an instance of object.
     * </p>
     * .
     *
     * @param clazz the class object to check is instance of
     * @param obj the base object type
     * @param message the exception message you would like to see if the
     * validation fails
     */
    public static void isInstanceOf(Class clazz, Object obj, String message) {
        notNull(clazz, "Type to check against must not be null");
        if (!clazz.isInstance(obj)) {
            throw new IllegalArgumentException(
                    message + "Object of class [" + (obj == null ? "null" : obj.getClass().getName())
                    + "] must be an instance of " + clazz);
        }
    }
}
