package de.bimalo.haushaltsbuch.transformator.delete;

import de.bimalo.haushaltsbuch.transformator.HaushaltsbuchRecord;
import de.bimalo.common.Assert;
import de.bimalo.common.Localizer;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
public final class HaushaltsbuchRecordsPrinter {

    /**
     * Default date pattern for conversions.
     */
    private final static String DEFAULT_DATE_PATTERN = "dd.MM.YY";

    /**
     * Logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(HaushaltsbuchRecordsPrinter.class);

    private static final Object[] FILE_HEADER = {"Konto", "Datum", "Buchungstext", "Hauptkostenart", "Unterkostenart", "Verwendungszweck", "Zahlungsempfänger", "Kontonummer", "BLZ", "Betrag"};

    private FileObject outputFile = null;

    /**
     * A Localizer deals with language specific values.
     */
    private Localizer localizer = new Localizer();

    public HaushaltsbuchRecordsPrinter(FileObject outputFile) {
        Assert.notNull(outputFile);
        this.outputFile = outputFile;
        try {
            if (!this.outputFile.exists()) {
                this.outputFile.createFolder();
            }
        } catch (FileSystemException ex) {
            logger.warn(ex.getMessage());
            if (logger.isTraceEnabled()) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void print(Haushaltsbuch hb) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT;

        OutputStream out = outputFile.getContent().getOutputStream();
        Writer writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

        CSVPrinter printer = new CSVPrinter(writer, format);
        printer.printRecord(FILE_HEADER);

        if (hb != null) {
            for (HaushaltsbuchRecord record : hb) {
                List<String> convertedRecord = convertRecord(record);
                printer.printRecord(convertedRecord);
            }
        }

    }

    private List<String> convertRecord(HaushaltsbuchRecord record) {
        List<String> convertedRecord = new ArrayList<String>();
        String konto = record.getKonto();
        convertedRecord.add(konto);

        Date datum = record.getDatum();
        String datumAsString = localizer.formatDateObject(datum, DEFAULT_DATE_PATTERN);
        convertedRecord.add(datumAsString);

        String buchungsText = record.getBuchungsText();
        convertedRecord.add(buchungsText);

        String hauptkostenArt = record.getHauptkostenArt();
        if (hauptkostenArt == null) {
            hauptkostenArt = "";
        }
        convertedRecord.add(hauptkostenArt);

        String unterkostenArt = record.getUnterkostenArt();
        if (unterkostenArt == null) {
            unterkostenArt = "";
        }
        convertedRecord.add(unterkostenArt);

        String verwendungszweck = record.getVerwendungszweck();
        convertedRecord.add(verwendungszweck);

        String zahlungsempfaenger = record.getZahlungsempfaenger();
        if (zahlungsempfaenger == null) {
            zahlungsempfaenger = "";
        }
        convertedRecord.add(zahlungsempfaenger);

        String kontonummer = record.getKontonummer();
        if (kontonummer == null) {
            kontonummer = "";
        }
        convertedRecord.add(kontonummer);

        String blz = record.getBlz();
        if (blz == null) {
            blz = "";
        }
        convertedRecord.add(blz);

        String betrag = record.getBetrag();
        convertedRecord.add(betrag);

        return convertedRecord;
    }
}
