package de.bimalo.haushaltsbuch.transformator;

import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessor;
import de.bimalo.common.Assert;

/*
 * <p>
 * Specifies the supported CSV file types.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public enum CSVFileTypes {

    SPARKASSE("de.bimalo.haushaltsbuch.transformator.SparkasseCSVLineProcessor"), COMDIRECT("de.bimalo.haushaltsbuch.transformator.ComdirectCSVLineProcessor");

    /**
     * The name of the class implementing the <code>CSVLineProcessor</code>
     * interface.
     */
    private String csvLineProcessorClassName = null;

    /**
     * Creates a new <code>CSVFileType</code> with a name of a class
     * implementing the <code>CSVLineProcessor</code> interface.
     *
     * @param csvLineProcessorClassName the class name implementing the
     * CSVLineProcessor
     */
    private CSVFileTypes(String csvLineProcessorClassName) {
        Assert.notNull(csvLineProcessorClassName);
        this.csvLineProcessorClassName = csvLineProcessorClassName;
    }

    /**
     * Provides a <code>CSVLineProcessor</code> implementation depending on the
     * defined type.
     *
     * @return a CSVLineProcessor implementation
     * @throws ClassNotFoundException the configured class name could not be
     * found in the class path.
     * @throws InstantiationException CSVLineProcessor implementation could not
     * be created.
     * @throws IllegalAccessException CSVLineProcessor implementation could not
     * be created.
     */
    public CSVLineProcessor getCSVLineProcessor() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class theClass = Class.forName(csvLineProcessorClassName);
        CSVLineProcessor theInstance = (CSVLineProcessor) theClass.newInstance();
        return theInstance;
    }

}
