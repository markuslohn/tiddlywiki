package de.bimalo.tiddlywiki.fs;

import de.bimalo.tiddlywiki.Tiddler;
import de.bimalo.tiddlywiki.common.Assert;
import de.bimalo.tiddlywiki.common.CommandLineParser;
import de.bimalo.tiddlywiki.common.TimeRecorder;
import de.bimalo.tiddlywiki.TiddlyWiki;
import de.bimalo.tiddlywiki.common.StreamUtilities;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Generates a TiddlyWiki based on a command line interface.</p>
 * <p>
 * See the function printUsage for the detail information about the required
 * arguments.</p>
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
    private static final Logger LOGGER = LoggerFactory.getLogger(TiddlyWikiGenerator.class);

    /**
     * Name of the argument for the root folder.
     */
    private static final String ROOTFOLDER_ARGUMENT = "rootFolder";
    /**
     * Name of the argument for the template file.
     */
    private static final String TEMPLATEFILE_ARGUMENT = "templateFile";
    /**
     * Name of the argument for the result file.
     */
    private static final String RESULTFILE_ARGUMENT = "resultFile";
    /**
     * Name of the argument for the max level.
     */
    private static final String MAXLEVEL_ARGUMENT = "maxLevel";

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
     * The maximum traversal level in the file system hierarchy. A negative
     * value means endless. Default is endless.
     */
    private int maxLevel = -1;

    /**
     * Creates a new <code>TiddlyWikiGenerator</code> with arguments provided as
     * <code>java.util.Map</code>.
     *
     * @param arguments Map containing arguments
     * @throws IllegalArgumentException if arguments is null or arguments does
     * not contain expected argument keys
     * @throws RuntimeException if any other error occurred
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
     * @throws RuntimeException if generation failed
     */
    public void run() {
        try {
            LOGGER.info("Start analyzing from {}...", rootFolder.getName().getPath());
            FilesystemTreeWalker traverser = new FilesystemTreeWalker(rootFolder);
            traverser.setMaxLevel(maxLevel);
            TiddlyWiki tw = traverser.walkFileTree();
            LOGGER.info("Done.");

            LOGGER.info("Create configuration for template engine...");
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_26);
            cfg.setDirectoryForTemplateLoading(new File(templateFile.getName().getParent().getPath()));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            Template temp = cfg.getTemplate(templateFile.getName().getBaseName());
            LOGGER.info("Done.");

            LOGGER.info("Write TiddlyWiki to file {}...", resultFile.getName().getPath());
            Writer out = null;
            try {
                out = new OutputStreamWriter(new FileOutputStream(resultFile.getName().getPath()), "UTF-8");
                Map root = new HashMap();
                root.put("title", tw.getTitle());
                root.put("subTitle", tw.getSubTitle());
                root.put("defaultTiddlers", tw.getDefaultTiddler().getTiddlers());
                root.put("rootTiddlers", tw.listTiddlers());
                root.put("staticFilesFolder", "/Users/mlohn/Google Drive/Referenz/_static");
                temp.process(root, out);
            } finally {
                StreamUtilities.closeWriter(out);
            }
            LOGGER.info("TiddlyWikie successfully written to {}.", resultFile.getName().getPath());
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
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
            out.write(sb.toString().getBytes("UTF8"));
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex.getMessage());
            System.out.println(sb.toString());
        } catch (IOException ex) {
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
        LOGGER.trace("resultFile= {}.", resultFileName);
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
        LOGGER.trace("tempalteFileName= {}.", templateFileName);
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
        LOGGER.trace("rootFolderName= {}.", rootFolderName);
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
        LOGGER.trace("maxLevel= {}.", maxLevelParamValue);
        if (maxLevelParamValue != null && !maxLevelParamValue.isEmpty()) {
            maxLevel = Integer.parseInt(maxLevelParamValue);
        }
    }

    /**
     * Starts the TiddlyWikiGenerator.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        VersionInfo vInfo = new VersionInfo();
        System.out.println("TiddlyWiki Generator " + vInfo.getVersionNumberString());
        LOGGER.info("TiddlyWiki Generator {}", vInfo.getVersionNumberString());

        if (args != null) {
            System.out.println("Arguments:");
            LOGGER.info("Arguments: ");
            for (String arg : args) {
                System.out.println(arg);
                LOGGER.info(arg);
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
            LOGGER.error(ex.getMessage());
            System.err.println(ex.getMessage());
            TiddlyWikiGenerator.printUsage(System.err);
        } finally {
            timerec.stop();
            LOGGER.info("{}", timerec);
            System.out.println(timerec);
        }
    }

}
