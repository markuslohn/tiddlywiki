package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.delete.CSVLineProcessorResult;
import de.bimalo.haushaltsbuch.transformator.HaushaltsbuchRecord;
import java.util.Date;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Processor for a single line of values from a CSV file provided by
 * Comdirect.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public class ComdirectCSVLineProcessor extends AbstractCSVLineProcessor {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(ComdirectCSVLineProcessor.class);

    @Override
    public CSVLineProcessorResult processLine(CSVRecord record) {
        HaushaltsbuchRecord haushaltsbuchRecord = new HaushaltsbuchRecord();
        CSVLineProcessorResult result = new CSVLineProcessorResult(haushaltsbuchRecord);

        logger.trace("Processing line {}...", record.getRecordNumber());
        haushaltsbuchRecord.setKonto("comdirect");

        Date buchungstag = getDateValue(record, result, "Buchungstag");
        haushaltsbuchRecord.setDatum(buchungstag);

        String vorgang = getStringValue(record, result, "Vorgang");
        haushaltsbuchRecord.setBuchungsText(vorgang);

        String buchungstext = getStringValue(record, result, "Buchungstext");
        haushaltsbuchRecord.setVerwendungszweck(buchungstext);

        String umsatz = getStringValue(record, result, "Umsatz");
        haushaltsbuchRecord.setBetrag(umsatz);

        logger.trace("Done.");

        return result;
    }

}
