package de.bimalo.haushaltsbuch.transformator;

import java.util.Date;

/**
 * <p>
 * Represents a single records in a CSV file provided by Sparkasse.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class SparkasseRecord {

    private String auftragskonto;
    private Date buchungstag;
    private String buchungstext;
    private String verwendungszweck;
    private String beguenstigter_zahlungspflichtiger;
    private String kontonummer;
    private String blz;
    private String betrag;

    public String getAuftragskonto() {
        return auftragskonto;
    }

    public void setAuftragskonto(String auftragskonto) {
        this.auftragskonto = auftragskonto;
    }

    public Date getBuchungstag() {
        return buchungstag;
    }

    public void setBuchungstag(Date buchungstag) {
        this.buchungstag = buchungstag;
    }

    public String getBuchungstext() {
        return buchungstext;
    }

    public void setBuchungstext(String buchungstext) {
        this.buchungstext = buchungstext;
    }

    public String getVerwendungszweck() {
        return verwendungszweck;
    }

    public void setVerwendungszweck(String verwendungszweck) {
        this.verwendungszweck = verwendungszweck;
    }

    public String getBeguenstigter_zahlungspflichtiger() {
        return beguenstigter_zahlungspflichtiger;
    }

    public void setBeguenstigter_zahlungspflichtiger(String beguenstigter_zahlungspflichtiger) {
        this.beguenstigter_zahlungspflichtiger = beguenstigter_zahlungspflichtiger;
    }

    public String getKontonummer() {
        return kontonummer;
    }

    public void setKontonummer(String kontonummer) {
        this.kontonummer = kontonummer;
    }

    public String getBlz() {
        return blz;
    }

    public void setBlz(String blz) {
        this.blz = blz;
    }

    public String getBetrag() {
        return betrag;
    }

    public void setBetrag(String betrag) {
        this.betrag = betrag;
    }

}
