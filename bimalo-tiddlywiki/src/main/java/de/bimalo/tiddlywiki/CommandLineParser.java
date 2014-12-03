package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * <p>
 * A parser for arguments provided by a command line interface. The arguments have
 * follow the syntax:</p>
 * <p>
 * <pre>-name=value</pre>
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class CommandLineParser {

    /**
     * The regular pattern describing the syntax of a command line argument:
     * -parametername=value
     */
    private final static String ARGUMENT_PATTERN = "-[\\w]+=[\\w\\W]+";

    /**
     * Represents java object representing the regular expression describing the
     * argument syntax.
     */
    private Pattern argumentPattern = null;

    /**
     * A <code>java.util.Map</code> containing all correct parsed arguments from
     * the command line. The name of the argument is the key and value is the
     * value provided by the command line.
     */
    private Map<String, String> argumentValues = new HashMap<String, String>();

    /**
     * Creates a new <code>CommandLineParser</code> with default values.
     *
     */
    public CommandLineParser() {
        argumentPattern = Pattern.compile(ARGUMENT_PATTERN);
    }

    /**
     * Parses a given array with arguments provided by the command line
     * interface.
     *
     * @param args array of arguments
     * @exception IllegalArgumentException if arguments are invalid
     */
    public void parseArguments(String[] args) {
        argumentValues.clear();
        for (int i = 0; i < args.length; i++) {
            parseArgument(args[i]);
        }
    }

    /**
     * Gets the value of the argument with the given name.
     *
     * @param name the name of the argument
     * @return value as String if argument exists or null otherwise
     * @exception IllegalArgumentException if name is null
     */
    public String getArgumentValue(String name) {
        Assert.notNull(name);
        return argumentValues.get(name);
    }

    /**
     * Gets all values retrieved from the command line as unmodifiable
     * <code>java.util.Map</code>.
     *
     * @return argument values as Map.
     */
    public Map<String, String> getArgumentValues() {
        return Collections.unmodifiableMap(argumentValues);
    }

    /**
     * Parses an given argument provided by the command line interface.
     *
     * @param argument the argument as String
     * @exception IllegalArgumentException if argument is invalid
     */
    private void parseArgument(String argument) {
        Matcher argumentMatcher = argumentPattern.matcher(argument);
        if (!argumentMatcher.matches()) {
            throw new IllegalArgumentException(constructInvalidSyntaxErrorMessage(argument));
        }
        int equalsSignIndex = argument.indexOf("=");
        String argumentName = argument.substring(1, equalsSignIndex);
        String argumentValue = argument.substring(equalsSignIndex + 1);
        argumentValues.put(argumentName, argumentValue);
    }

    private String constructInvalidSyntaxErrorMessage(String argument) {
        StringBuilder sb = new StringBuilder();
        sb.append("Argument ");
        sb.append(argument);
        sb.append(" does not follow the argument syntax ");
        sb.append("-argumentName=argumentValue.");
        return sb.toString();
    }

}
