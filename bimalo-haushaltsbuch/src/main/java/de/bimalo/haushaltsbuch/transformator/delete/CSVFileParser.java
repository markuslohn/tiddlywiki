package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.delete.Haushaltsbuch;
import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessorResult;
import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessor;
import de.bimalo.haushaltsbuch.transformator.HaushaltsbuchRecord;
import de.bimalo.common.Assert;
import de.bimalo.common.IOUtilities;
import de.bimalo.haushaltsbuch.transformator.CSVFileTypes;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class CSVFileParser {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(CSVFileParser.class);

    /**
     * Reference to the CSV file to parse.
     */
    private FileObject inputFile = null;
    /**
     * The type of the CSV file to modify the parsing process.
     */
    private CSVFileTypes inputFileType = CSVFileTypes.SPARKASSE;

    /**
     * The character defines the boundary between values in a record.
     */
    private char delimiterChar = ';';

    /**
     * Creates a new <code>CSVFileParser</code>.
     *
     * @param inputFile the CSV input file
     * @param inputFileType the type of the CSV file, if null a default value
     * will be used (CSVFileTypes.SPARKASSE).
     * @throws IOException if inputFile is null or if inputFile doesn't exist
     */
    public CSVFileParser(FileObject inputFile, CSVFileTypes inputFileType) throws IOException {
        Assert.notNull(inputFile);
        Assert.isTrue(inputFile.exists());
        if (!(inputFile.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(inputFile.getName().getPath());
        }

        this.inputFile = inputFile;
        if (inputFileType != null) {
            this.inputFileType = inputFileType;
        }

    }

    /**
     * Sets a new delimiter char used to extract the values within a line.
     *
     * @param delimiter the new delimiter char
     */
    public void setDelimiter(char delimiter) {
        this.delimiterChar = delimiter;
    }

    public Object parse() {
        Haushaltsbuch hb = new Haushaltsbuch();
        List<CSVLineProcessorResult> faultedLines = new ArrayList<CSVLineProcessorResult>();

        try {
            CSVLineProcessor lineProcessor = prepareCSVLineProcessor();

            Reader reader = buildReader(inputFile);
            try {
                logger.trace("Iterate through the records in CSV file {}...", inputFile.getName().getPath());
                long processed = 0;
                Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter(delimiterChar).withHeader().parse(reader);
                for (CSVRecord record : records) {
                    CSVLineProcessorResult lineResult = lineProcessor.processLine(record);
                    if (lineResult != null) {
                        if (!lineResult.isFaulted()) {
                            Object resultObj = lineResult.getResult();
                            if (resultObj instanceof HaushaltsbuchRecord) {
                                hb.add((HaushaltsbuchRecord) resultObj);
                            }
                        } else {
                            faultedLines.add(lineResult);
                        }
                    }
                    processed++;
                }
                logger.trace("Done.");
            } finally {
                IOUtilities.closeReader(reader);
            }

        } catch (IOException ex) {
            throw new RuntimeException("Couldn't read CSV file.", ex);
        }

        return hb;
    }

    /**
     * Creates a <code>java.io.Reader</code> based on a given
     * <code>org.apache.commons.vfs2.FileObject</code>.
     *
     * @param file the FileObject representing a file
     * @return the new created java.io.Reader
     * @throws FileSystemException if file could not be read
     */
    private Reader buildReader(FileObject file) throws FileSystemException {
        InputStream is = file.getContent().getInputStream();
        InputStreamReader isReader = new InputStreamReader(is);
        return isReader;
    }

    /**
     * Prepares a CSVLineProcessor implementation based on the defined
     * <code>CSVFileType</code>.
     *
     * @return the CSVLineProcessor implementation class.
     */
    private CSVLineProcessor prepareCSVLineProcessor() {
        try {
            logger.trace("Initializes the CSVLineProcessor...");
            CSVLineProcessor lineProcessor = inputFileType.getCSVLineProcessor();
            logger.info("CSVLineProcessor= {}.", lineProcessor.getClass().getName());
            logger.trace("Done.");

            return lineProcessor;

        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("CSVLineProcessor not found in the classpath.", ex);
        } catch (InstantiationException ex) {
            throw new RuntimeException("CSVLineProcessor couldn't be initialized.", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("CSVLineProcessor couldn't be loaded.", ex);
        }
    }

}

