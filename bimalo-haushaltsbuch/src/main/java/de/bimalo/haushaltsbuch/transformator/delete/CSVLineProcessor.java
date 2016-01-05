package de.bimalo.haushaltsbuch.transformator.delete;

import org.apache.commons.csv.CSVRecord;

/**
 * <p>
 * A processor for a single line with values read from a CSV file.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public interface CSVLineProcessor {

    /**
     * Processes exactly one line with values read from a CSV file.
     *
     * @param record containing the values of the line read from a CSV file.
     * @return a CSVLineProcessorResult containing the outcome for the processed
     * line
     */
    public CSVLineProcessorResult processLine(CSVRecord record);
}
