/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessorResult;
import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessor;
import de.bimalo.common.Assert;
import de.bimalo.common.Localizer;
import java.util.Date;
import org.apache.commons.csv.CSVRecord;

/**
 * <p>
 * Implements conversion functions that can be used when implementing a concrete
 * <code>CSVLineProcessor</code> implementation.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 * @see HaushaltsbuchRecords
 */
public abstract class AbstractCSVLineProcessor implements CSVLineProcessor {

    /**
     * Default date pattern for conversions.
     */
    private final static String DEFAULT_DATE_PATTERN = "dd.MM.YY";

    /**
     * A Localizer deals with language specific values.
     */
    private Localizer localizer = new Localizer();

    /**
     * Gets the value for the requested columnName.
     *
     * @param record contains the values for a single line read from a CSV file
     * @param result the result object to track errors
     * @param columnName the requested column name
     * @return the string value or null if not found any value
     */
    public String getStringValue(CSVRecord record, CSVLineProcessorResult result, String columnName) {
        Assert.notNull(record);
        Assert.notNull(result);
        Assert.notNull(columnName);

        String value = null;

        try {
            value = record.get(columnName);
        } catch (Throwable th) {
            result.add(th, columnName);
        }
        return value;
    }

    /**
     * Gets the value for the requested columnName.
     *
     * @param record contains the values for a single line read from a CSV file
     * @param result the result object to track errors
     * @param columnName the requested column name
     * @return the Date value or null if not found any value
     */
    public Date getDateValue(CSVRecord record, CSVLineProcessorResult result, String columnName) {
        Assert.notNull(record);
        Assert.notNull(result);
        Assert.notNull(columnName);

        Date dateValue = null;

        try {
            String strValue = record.get(columnName);
            dateValue = localizer.parseDateString(strValue, "dd.MM.YY");

        } catch (Throwable th) {
            result.add(th, columnName);
        }
        return dateValue;
    }

}
