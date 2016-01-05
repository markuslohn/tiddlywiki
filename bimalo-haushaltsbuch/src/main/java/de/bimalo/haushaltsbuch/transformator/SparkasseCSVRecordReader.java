package de.bimalo.haushaltsbuch.transformator;

import de.bimalo.common.Localizer;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
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
public final class SparkasseCSVRecordReader implements CSVRecordReader {

    /**
     * Default date pattern for conversions.
     */
    private final static String DEFAULT_DATE_PATTERN = "dd.MM.YY";

    /**
     * A Localizer deals with language specific values.
     */
    private Localizer localizer = new Localizer();

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(SparkasseCSVRecordReader.class);

    @Override
    public Object read(Map record) {
        SparkasseRecord spkRecord = new SparkasseRecord();

        spkRecord.setAuftragskonto((String) record.get("Auftragskonto"));
        spkRecord.setBeguenstigter_zahlungspflichtiger((String) record.get("Beguenstigter/Zahlungspflichtiger"));
        spkRecord.setBetrag((String) record.get("Betrag"));
        spkRecord.setBlz((String) record.get("BLZ"));
        String stringValue = (String) record.get("Buchungstag");
        Date dateValue = null;
        try {
            dateValue = localizer.parseDateString(stringValue, "dd.MM.YY");
            spkRecord.setBuchungstag(dateValue);
        } catch (ParseException ex) {
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            } else {
                logger.warn(ex.getMessage());
            }
        }
        String buchungstext = (String) record.get("Buchungstext");
        spkRecord.setBuchungstext(buchungstext);
        spkRecord.setKontonummer((String) record.get("Kontonummer"));
        spkRecord.setVerwendungszweck((String) record.get("Verwendungszweck"));

        return spkRecord;
    }
}
