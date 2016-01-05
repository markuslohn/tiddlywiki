package de.bimalo.haushaltsbuch.transformator;

import de.bimalo.common.Assert;
import de.bimalo.common.IOUtilities;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.vfs2.FileNotFolderException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.VFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * A CSVFile allows to read and write csv files. The read and write functions
 * can be customized to enable a format specific processing of csv records.
 * </p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see de.bimalo.haushaltsbuch.transformator.CSVRecordReader
 * @see de.bimalo.haushaltsbuch.transformator.
 */
public final class CSVFile {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(CSVFile.class);

    /**
     * The reference to the CSV file in the local file system.
     */
    private FileObject file = null;

    /**
     * The character defines the boundary between values in a record.
     */
    private char delimiterChar = ';';

    /**
     * Creates a new <code>CSVFile</code>.
     *
     * @param pathname A pathname string
     * @exception IllegalArgumentException if pathname is null.
     * @exception IOException on error parsing the file name.
     */
    public CSVFile(String pathname) throws IOException {
        Assert.notNull(pathname);
        String pathnameURI = convert2URI(pathname);
        FileSystemManager fsManager = VFS.getManager();
        file = fsManager.resolveFile(pathnameURI);
        if (file.exists() && !(file.getType().equals(FileType.FILE))) {
            throw new FileNotFolderException(file.getName().getPath());
        }
    }

    /**
     * Sets a new delimiter char used to extract the values within a line. As
     * default delimiter char ";" is used.
     *
     * @param delimiter the new delimiter char
     */
    public void setDelimiter(char delimiter) {
        this.delimiterChar = delimiter;
    }

    /**
     * Gets the absolute path to this CSVFile.
     *
     * @return the absolute pathname
     */
    public String getPath() {
        return file.getName().getPath();
    }

    /**
     * Deletes this CSVFile.
     *
     * @return true if successfully deleted, otherwise false
     * @throws IOException if operation fails
     */
    public boolean delete() throws IOException {
        return file.delete();
    }

    /**
     * Moves this CSVFile to another location.
     *
     * @param pathname the absolute pathname to the new location.
     * @throws IOException if operation fails
     */
    public void moveTo(String pathname) throws IOException {
        Assert.notNull(pathname);
        String pathnameURI = convert2URI(pathname);
        FileSystemManager fsManager = VFS.getManager();
        FileObject target = fsManager.resolveFile(pathnameURI);
        file.moveTo(target);
        file = target;
    }

    /**
     * Provides an information about the existence of this CSVFile.
     *
     * @return true = file exists otherwise false
     * @throws IOException if operation fails
     */
    public boolean exists() throws IOException {
        return file.exists();
    }

    /**
     * Reads the records from this CSVFile and provides each record as object in
     * a <code>java.util.List</code>. When a <code>CSVRecordReader</code> was
     * provided it will be called to transform every record into a specific
     * object. If not provided every record is provided as
     * <code>java.util.HashMap</code>.
     *
     * @param recordReader a format specific CSVRecordReader to create specific
     * objects for a csv record.
     * @return a java.util.List with objects containing the records read from
     * the CSVFile.
     * @throws IOException if operation fails.
     */
    public List readRecords(CSVRecordReader recordReader) throws IOException {
        List recordsList = new ArrayList();

        Reader fileReader = buildReader(file);

        try {
            logger.trace("Iterate throught records from file {}...", getPath());
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withDelimiter(delimiterChar).withHeader().parse(fileReader);
            for (CSVRecord record : records) {
                logger.trace("Working on record {}...", record.getRecordNumber());
                Map recordValues = record.toMap();

                if (recordReader != null) {
                    logger.trace("Use CSVRecordReader {}.", recordReader.getClass().getName());
                    recordsList.add(recordReader.read(recordValues));
                } else {
                    logger.trace("Use a java.util.Map.");
                    recordsList.add(recordValues);
                }
                logger.trace("Done.");
            }
            logger.trace("Done.");
        } finally {
            IOUtilities.closeReader(fileReader);
        }

        return recordsList;
    }

    /**
     * Writes given records into this CSVFile. The record values can be
     * customized by providing a specific <code>CSVRecordWriter</code>.
     *
     * @param records a java.util.List containing objects whereas every object
     * is a single csv record
     * @param recordWriter used to retrieve csv record values from a object.
     * @throws IOException if operation fails
     */
    public void writeRecords(List<Object> records, CSVRecordWriter recordWriter) throws IOException {
        writeRecords(Collections.EMPTY_LIST, records, recordWriter);
    }

    /**
     * Writes given records into this CSVFile. The record values can be
     * customized by providing a specific <code>CSVRecordWriter</code>.
     *
     * @param header an optional header line for the csv file.
     * @param records a java.util.List containing objects whereas every object
     * is a single csv record
     * @param recordWriter used to retrieve csv record values from a object.
     * @throws IOException if operation fails
     */
    public void writeRecords(List<String> header, List<Object> records, CSVRecordWriter recordWriter) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT;
        OutputStream out = null;
        Writer writer = null;
        CSVPrinter printer = null;

        try {
            prepareFileForWriting();

            out = file.getContent().getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            printer = new CSVPrinter(writer, CSVFormat.DEFAULT);

            if (header != null && !header.isEmpty()) {
                logger.trace("Prints header in the csv file {}...", getPath());
                printer.printRecord(header);
                logger.trace("Done.");
            }

            if (records != null) {
                logger.trace("Prints records into the csv file {}...", getPath());
                for (Object record : records) {
                    if (recordWriter != null) {
                        List<String> recordValues = recordWriter.write(record);
                        printer.printRecord(recordValues);
                    } else {
                        printer.printRecord(record);
                    }
                }
                logger.trace("Done");
            }
        } finally {
            closeCSVPrinter(printer);
            IOUtilities.closeOutputStream(out);
            IOUtilities.closeWriter(writer);
        }
    }

    /**
     * Converts a given path name into a URI representation if possible.
     *
     * @param pathname the path name
     * @return the URI representation of the path name as String
     */
    private String convert2URI(String pathname) {
        String uri = pathname;
        try {
            uri = new URI(pathname).toString();
        } catch (URISyntaxException ex) {
            if (logger.isTraceEnabled()) {
                logger.error("Could not create URI for {}. Try to resolve the file without URI conversion.", pathname);
                logger.error(ex.getMessage(), ex);
            }
        }
        return uri;
    }

    /**
     * Deletes this CSVFile when available.
     *
     * @throws IOException if operation fails
     */
    private void prepareFileForWriting() throws IOException {
        if (file.exists()) {
            logger.debug("Deletes the file {}.", file.getName().getPath());
            file.delete();
        }
    }

    /**
     * Helper function to close a CSVPrinter.
     *
     * @param printer the CSVPrinter to close.
     */
    private void closeCSVPrinter(CSVPrinter printer) {
        try {
            if (printer != null) {
                printer.close();
            }
        } catch (IOException ex) {
            logger.warn("An InputStream could not be closed!");
        }
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

}
