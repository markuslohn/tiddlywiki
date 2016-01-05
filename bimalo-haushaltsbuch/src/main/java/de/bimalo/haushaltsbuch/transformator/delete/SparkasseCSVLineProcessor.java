package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.HaushaltsbuchRecord;
import de.bimalo.common.Localizer;
import java.util.Date;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Processor for a single line of values from a CSV file provided by Sparkasse.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class SparkasseCSVLineProcessor extends AbstractCSVLineProcessor {

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(SparkasseCSVLineProcessor.class);

    @Override
    public CSVLineProcessorResult processLine(CSVRecord record) {
        HaushaltsbuchRecord haushaltsbuchRecord = new HaushaltsbuchRecord();
        CSVLineProcessorResult result = new CSVLineProcessorResult(haushaltsbuchRecord);

        logger.trace("Processing line {}...", record.getRecordNumber());
        String auftragskonto = getStringValue(record, result, "Auftragskonto");
        haushaltsbuchRecord.setKonto(auftragskonto);

        Date buchungstag = getDateValue(record, result, "Buchungstag");
        haushaltsbuchRecord.setDatum(buchungstag);

        String buchungstext = getStringValue(record, result, "Buchungstext");
        haushaltsbuchRecord.setBuchungsText(buchungstext);

        String verwendungszweck = getStringValue(record, result, "Verwendungszweck");
        haushaltsbuchRecord.setVerwendungszweck(verwendungszweck);

        String beguenstigter = getStringValue(record, result, "Beguenstigter/Zahlungspflichtiger");
        haushaltsbuchRecord.setZahlungsempfaenger(beguenstigter);

        String kontonummer = getStringValue(record, result, "Kontonummer");
        haushaltsbuchRecord.setKontonummer(kontonummer);

        String blz = getStringValue(record, result, "BLZ");
        haushaltsbuchRecord.setBlz(blz);

        String betrag = getStringValue(record, result, "Betrag");
        haushaltsbuchRecord.setBetrag(betrag);

        defineKostenarten(buchungstext, haushaltsbuchRecord);
        logger.trace("Done.");

        return result;
    }

    private void defineKostenarten(String buchungstext, HaushaltsbuchRecord record) {
        if ("LOHN GEHALT".equalsIgnoreCase(buchungstext) || "LOHN/GEHALT".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Einnahmen");
            record.setUnterkostenArt("Gehalt");
        }
        if ("GELDAUTOMAT".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Geldautomat");
        }
        if ("EINZUG RATE/ANNUITAET".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Verbindlichkeiten");
        }
        if ("ENTGELTABSCHLUSS".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Bankgebühren");
        }
        if ("GUTSCHRIFT".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Einnahmen");
        }
        if ("SONSTIGE BELASTUNGSBUCHUNG".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Sparen");
        }
        if ("UEBERWEISUNGSGUTSCHRIFT".equalsIgnoreCase(buchungstext)) {
            record.setHauptkostenArt("Einnahmen");
        }

    }
}
