package de.bimalo.tiddlywiki.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * <p> A parser for arguments provided by a command line interface. The arguments have follow the
 * syntax:</p> <p> <pre>-name=value</pre> </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * 
 * @since 1.0
 */
public final class CommandLineParser {

    /**
     * The regular pattern describing the syntax of a command line argument:
     * -parametername=value.
     */
    private static final String ARGUMENT_PATTERN = "-[\\w]+=[\\w\\W]+";

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
    private final Map<String, String> argumentValues = new HashMap<>();

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
        for (String arg : args) {
            parseArgument(arg);
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
        String[] values = argument.split("=");
        String argumentName = values[0].substring(1);
        String argumentValue = values[1];
        if (argumentValue.endsWith(".properties")) {
            File propertiesFile = resolvePropertyFile(argumentValue);
            if (propertiesFile.exists()) {
                Properties props = new Properties();
                try {
                    props.load(new FileInputStream(propertiesFile));
                    props.entrySet().forEach((entry) -> {
                        argumentValues.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    });
                } catch (FileNotFoundException ex) {
                    // Can be ignored because verified before calling this function!
                } catch (IOException ex) {
                    argumentValues.put(argumentName, argumentValue);
                }

            } else {
                argumentValues.put(argumentName, argumentValue);
            }
        } else {
            argumentValues.put(argumentName, argumentValue);
        }
    }

    private File resolvePropertyFile(String path) {
        File propertyFile = new File(path);
        if (!propertyFile.exists()) {
            String workingFolderName = System.getProperty("working.dir");
            if (workingFolderName != null) {
                File workingFolder = new File(workingFolderName);
                if (workingFolder.exists()) {
                    propertyFile = new File(workingFolder, path);
                }
            }
        }

        return propertyFile;
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
