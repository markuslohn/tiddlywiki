package de.bimalo.haushaltsbuch.transformator;

import java.io.OutputStream;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.bimalo.common.Assert;
import de.bimalo.common.TimeRecorder;
import de.bimalo.common.CommandLineParser;
import java.io.IOException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

/**
 * <p>
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class HaushaltsbuchTransformator {

    /**
     * Logger instance.
     */
    private final static Logger logger = LoggerFactory.getLogger(HaushaltsbuchTransformator.class);

    private final static String SOURCEFOLDER_ARGUMENT = "sourceFolder";

    private final static String RESULTFOLDER_ARGUMENT = "resultFolder";

    private final static String ARCHIVEFOLDER_ARGUMENT = "archiveFolder";

    /**
     * A pathname to a folder containing CSV files to transform.
     */
    private FileObject sourceFolder = null;

    /**
     * A pathname to a folder used to save the transformed CSV files.
     */
    private FileObject resultFolder = null;

    /**
     * A pathname to a folder used to save the origin CSV files.
     */
    private FileObject archiveFolder = null;

    /**
     * Creates a new <code>HaushaltsbuchTransformator</code> with arguments
     * provided as <code>java.util.Map</code>.
     *
     * @param arguments Map containing arguments
     * @exception IllegalArgumentException if arguments is null or arguments
     * does not contain expected argument keys
     * @exception RuntimeException if any other error occurred
     */
    public HaushaltsbuchTransformator(Map<String, String> arguments) {
        Assert.notNull(arguments);
        Assert.isTrue(arguments.containsKey(SOURCEFOLDER_ARGUMENT), "Argument sourceFolder is missing.");
        Assert.isTrue(arguments.containsKey(RESULTFOLDER_ARGUMENT), "Argument resultFolder is missing.");
        Assert.isTrue(arguments.containsKey(ARCHIVEFOLDER_ARGUMENT), "Argument archiveFolder is missing.");

        try {

            initSourceFolderArgument(arguments);

            initResultFolderArgument(arguments);

            initArchiveFolderArgument(arguments);

        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     *
     *
     * @exception RuntimeException if generation failed
     */
    public void run() {
        try {
            logger.info("Start analyzing from {}...");

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
        sb.append(HaushaltsbuchTransformator.class).append(" Usage: \n");
        sb.append("./hbtf -sourceFolder=<value> -targetFolder=<value> -archiveFolder=<value> \n");
        sb.append("\n");
        sb.append("sourceFolder = A pathname to a folder containing CSV files to transform. \n");
        sb.append("targetFolder = A pathname to a folder used to save the transformed CSV files. \n");
        sb.append("archiveFolder = A pathname to a folder used to save the origin CSV files. \n");
        sb.append("\n");
        sb.append("Example: \n");
        sb.append("./hbtf -sourceFolder=<value> -targetFolder=<value> -archiveFolder=<value> \n");
        try {
            out.write(sb.toString().getBytes());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            System.out.println(sb.toString());
        }
    }

    public final static void main(String[] args) {
        VersionInfo vInfo = new VersionInfo();
        System.out.println("Haushaltsbuch Transformator " + vInfo.getVersionNumberString());

        if (args != null) {
            System.out.println("Arguments:");
            for (String arg : args) {
                System.out.println(arg);
            }
        }

        TimeRecorder timerec = new TimeRecorder("Haushaltsbuch Transformator");
        timerec.start();

        try {
            CommandLineParser cmdParser = new CommandLineParser();
            cmdParser.parseArguments(args);

            HaushaltsbuchTransformator twgen = new HaushaltsbuchTransformator(cmdParser.getArgumentValues());
            twgen.run();
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            HaushaltsbuchTransformator.printUsage(System.err);
        } finally {
            timerec.stop();
            System.out.println(timerec);
        }
    }

    /**
     * Retrieves the argument sourcefolder.
     *
     * @param arguments a Map containing all arguments
     * @throws IOException if operation failed
     */
    private void initSourceFolderArgument(Map<String, String> arguments) throws IOException {
        FileSystemManager fsManager = VFS.getManager();

        String sourceFolderName = arguments.get(SOURCEFOLDER_ARGUMENT);
        logger.trace("sourceFolderName= {}.", sourceFolderName);
        sourceFolder = fsManager.resolveFile(sourceFolderName);
        if (!sourceFolder.exists()) {
            throw new IllegalArgumentException(sourceFolder + " doesn't exist.");
        }
    }

    /**
     * Retrieves the argument resultFolder.
     *
     * @param arguments a Map containing all arguments
     * @throws IOException if operation failed
     */
    private void initResultFolderArgument(Map<String, String> arguments) throws IOException {
        FileSystemManager fsManager = VFS.getManager();

        String resultFolderName = arguments.get(RESULTFOLDER_ARGUMENT);
        logger.trace("resultFolderName= {}.", resultFolderName);
        resultFolder = fsManager.resolveFile(resultFolderName);
        if (!resultFolder.exists()) {
            throw new IllegalArgumentException(resultFolder + " doesn't exist.");
        }
    }

    /**
     * Retrieves the argument archiveFolder.
     *
     * @param arguments a Map containing all arguments
     * @throws IOException if operation failed
     */
    private void initArchiveFolderArgument(Map<String, String> arguments) throws IOException {
        FileSystemManager fsManager = VFS.getManager();

        String archiveFolderName = arguments.get(ARCHIVEFOLDER_ARGUMENT);
        logger.trace("archiveFolderName= {}.", archiveFolderName);
        archiveFolder = fsManager.resolveFile(archiveFolderName);
        if (!archiveFolder.exists()) {
            throw new IllegalArgumentException(archiveFolder + " doesn't exist.");
        }
    }

}
