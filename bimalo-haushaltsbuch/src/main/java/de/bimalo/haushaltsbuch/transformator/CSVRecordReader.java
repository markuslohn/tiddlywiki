package de.bimalo.haushaltsbuch.transformator;

import java.util.Map;

/**
 * <p>
 * A CSVRecordReader can be used to customize the process when reading records
 * from a CSVFile.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see de.bimalo.haushaltsbuch.transformator.CSVFile
 */
public interface CSVRecordReader {
   
    /**
     * Creates a new object based on the values from a csv record.
     * @param record the values of a csv record 
     * @return the new object
     */
    public Object read(Map record);
    
}
