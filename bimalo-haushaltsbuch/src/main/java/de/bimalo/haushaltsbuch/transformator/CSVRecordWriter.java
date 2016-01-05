package de.bimalo.haushaltsbuch.transformator;

import java.util.List;

/**
 * <p>
 * A CSVRecordWriter can be used to customize the process when writing a record
 * in a CSVFile.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see de.bimalo.haushaltsbuch.transformator.CSVFile
 */
public interface CSVRecordWriter {
    
    /**
     * Prepares a single csv record for writing based on a given object.
     * @param obj the object containing the values for the csv record
     * @return a java.util.List with values for a single csv record
     */
    public List<String> write(Object obj);
}
