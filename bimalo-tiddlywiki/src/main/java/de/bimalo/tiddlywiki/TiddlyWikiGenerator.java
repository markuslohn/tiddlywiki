package de.bimalo.tiddlywiki;

import de.bimalo.common.Assert;
import de.bimalo.common.IOUtilities;
import de.bimalo.common.TimeRecorder;
import java.io.OutputStream;
import java.util.Map;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * <p>
 * Generates a TiddlyWiki based on a command line interface.</p>
 * <p>
 * See the function printUsage for the detail information about the
 * required arguments.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see TiddlyWiki
 * @see Tiddler
 */
public final class TiddlyWikiGenerator {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(TiddlyWikiGenerator.class);

    private final static String ROOTFOLDER_ARGUMENT = "rootFolder";

    private final static String TEMPLATEFILE_ARGUMENT = "templateFile";

    private final static String RESULTFILE_ARGUMENT = "resultFile";

    private final static String MAXLEVEL_ARGUMENT = "maxLevel";

    /**
     * The absolute path to the folder used for the TiddlyWiki content.
     */
    private FileObject rootFolder = null;
    /**
     * The absolute path to the TiddlyWiki template file.
     */
    private FileObject templateFile = null;
    /**
     * The absolute path to the TiddlyWiki result file.
     */
    private FileObject resultFile = null;
    /**
     * The maximum traversal level in the file system hierarchy. A negativ value
     * means endless. Default is endless.
     */
    private int maxLevel = -1;

    /**
     * Creates a new <code>TiddlyWikiGenerator</code> with arguments provided as
     * <code>java.util.Map</code>.
     *
     * @param arguments Map containing arguments
     * @exception IllegalArgumentException if arguments is null or arguments
     * does not contain expected argument keys
     * @exception RuntimeException if any other error occurred
     */
    public TiddlyWikiGenerator(Map<String, String> arguments) {
        Assert.notNull(arguments);
        Assert.isTrue(arguments.containsKey(ROOTFOLDER_ARGUMENT), "Argument rootFolder is missing.");
        Assert.isTrue(arguments.containsKey(TEMPLATEFILE_ARGUMENT), "Argument templateFile is missing.");
        Assert.isTrue(arguments.containsKey(RESULTFILE_ARGUMENT), "Argument resultFile is missing.");

        try {

            initRootFolderArgument(arguments);

            initTemplateFileArgument(arguments);

            initResultFileArgument(arguments);

            initMaxLevelArgument(arguments);

        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (FileSystemException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Start the process of generating a TiddlyWiki.
     *
     * @exception RuntimeException if generation failed
     */
    public void run() {
        try {
            logger.info("Start analyzing from {}...", rootFolder.getName().getPath());
            FilesystemTraversal traverser = new FilesystemTraversal(rootFolder);
            traverser.setMaxLevel(maxLevel);
            TiddlyWiki tw = traverser.traverse();
            logger.info("Done.");

            logger.info("Write TiddlyWiki to file {}...", resultFile.getName().getPath());
            OutputStream os = null;
            try {
                os = resultFile.getContent().getOutputStream();
                DefaultTiddlyWikiSerializer serializer = new DefaultTiddlyWikiSerializer(tw, templateFile);
                serializer.writeObject(os);
                os.flush();
            } finally {
                IOUtilities.closeOutputStream(os);
            }
            logger.info("Done.");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Prints a help message about how to use the class.
     *
     * @param out the OutputStream to print out the usage information
     */
    public static void printUsage(OutputStream out) {
        Assert.notNull(out);

        StringBuilder sb = new StringBuilder();
        sb.append(TiddlyWikiGenerator.class).append(" Usage: \n");
        sb.append("./tw -rootFolder=<value> -templateFile=<value> -resultFile=<value> \n");
        sb.append("\n");
        sb.append("rootFolder = The absolute path to the folder containing the content for the TiddlyWiki. \n");
        sb.append("templateFile = The absolute path to an empty TiddlyWiki file. \n");
        sb.append("resultFile = The absolute path to the result TiddlyWiki file. \n");
        sb.append("\n");
        sb.append("Example: \n");
        sb.append("./tw -rootFolder=<value> -templateFile=<value> -resultFile=<value> \n");
        try {
            out.write(sb.toString().getBytes());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(sb.toString());
        }
    }

    /**
     * Retrieves the argument describing the result file.
     *
     * @param arguments a Map containing all arguments
     * @throws FileSystemException if operation failed
     */
    private void initResultFileArgument(Map<String, String> arguments) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();

        String resultFileName = arguments.get(RESULTFILE_ARGUMENT);
        logger.trace("resultFile= {}.", resultFileName);
        resultFile = fsManager.resolveFile(resultFileName);
    }

    /**
     * Retrieves the argument describing the template file.
     *
     * @param arguments a Map containing all arguments
     * @throws FileSystemException if operation failed
     */
    private void initTemplateFileArgument(Map<String, String> arguments) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();

        String templateFileName = arguments.get(TEMPLATEFILE_ARGUMENT);
        logger.trace("tempalteFileName= {}.", templateFileName);
        templateFile = fsManager.resolveFile(templateFileName);
        if (!templateFile.exists()) {
            throw new IllegalArgumentException(templateFile + " doesn't exist.");
        }
    }

    /**
     * Retrieves the argument describing the root folder.
     *
     * @param arguments a Map containing all arguments
     * @throws FileSystemException if operation failed
     */
    private void initRootFolderArgument(Map<String, String> arguments) throws FileSystemException {
        FileSystemManager fsManager = VFS.getManager();

        String rootFolderName = arguments.get(ROOTFOLDER_ARGUMENT);
        logger.trace("rootFolderName= {}.", rootFolderName);
        rootFolder = fsManager.resolveFile(rootFolderName);
        if (!rootFolder.exists()) {
            throw new IllegalArgumentException(rootFolderName + " doesn't exist.");
        }
    }

    /**
     * Retrieves the argument defining the maximum level of file system
     * hierarchy traversal.
     *
     * @param arguments a Map containing all arguments
     */
    private void initMaxLevelArgument(Map<String, String> arguments) {
        String maxLevelParamValue = arguments.get(MAXLEVEL_ARGUMENT);
        logger.trace("maxLevel= {}.", maxLevelParamValue);
        if (maxLevelParamValue != null && !maxLevelParamValue.isEmpty()) {
            try {
                maxLevel = Integer.parseInt(maxLevelParamValue);
            } catch (Exception ex) {
                logger.warn("Parameter maxLevel not set because of ", ex);
            }
        }
    }

    public final static void main(String[] args) {
        VersionInfo vInfo = new VersionInfo();
        System.out.println("TiddlyWiki Generator " + vInfo.getVersionNumberString());

        if (args != null) {
            System.out.println("Arguments:");
            for (int i = 0; i < args.length; i++) {
                System.out.println(args[i]);
            }
        }

        TimeRecorder timerec = new TimeRecorder("TiddlyWiki");
        timerec.start();

        try {
            CommandLineParser cmdParser = new CommandLineParser();
            cmdParser.parseArguments(args);

            TiddlyWikiGenerator twgen = new TiddlyWikiGenerator(cmdParser.getArgumentValues());
            twgen.run();
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            TiddlyWikiGenerator.printUsage(System.err);
        } finally {
            timerec.stop();
            System.out.println(timerec);
        }
    }

}
