package de.bimalo.haushaltsbuch.transformator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/*
 * <p>
 * Represents a single account record in the book of household accounts.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class HaushaltsbuchRecord {
    
    /**
     * Column "Konto".
     */
    private String konto;
    /**
     * Column "Datum";
     */
    private Date datum;
    /**
     * Column "Buchungstext";
     */
    private String buchungsText;
    /**
     * Column "Hauptkostenart";
     */
    private String hauptkostenArt;
    /**
     * Column "Unterkostenart";
     */
    private String unterkostenArt;
    /**
     * Column Verwendungszweck";
     */
    private String verwendungszweck;
    /**
     * Column Zahlungsempfaenger".
     */
    private String zahlungsempfaenger;
    /**
     * Column "Kontonummer".
     */
    private String kontonummer;
    /**
     * Column "BLZ".
     */
    private String blz;
    /**
     * Column "Betrag".
     */
    private String betrag;
    
    private int hashCode = -1;

    public String getKonto() {
        return konto;
    }

    public void setKonto(String konto) {
        this.konto = konto;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }

    public String getBuchungsText() {
        return buchungsText;
    }

    public void setBuchungsText(String buchungsText) {
        this.buchungsText = buchungsText;
    }

    public String getHauptkostenArt() {
        return hauptkostenArt;
    }

    public void setHauptkostenArt(String hauptkostenArt) {
        this.hauptkostenArt = hauptkostenArt;
    }

    public String getUnterkostenArt() {
        return unterkostenArt;
    }

    public void setUnterkostenArt(String unterkostenArt) {
        this.unterkostenArt = unterkostenArt;
    }

    public String getVerwendungszweck() {
        return verwendungszweck;
    }

    public void setVerwendungszweck(String verwendungszweck) {
        this.verwendungszweck = verwendungszweck;
    }

    public String getZahlungsempfaenger() {
        return zahlungsempfaenger;
    }

    public void setZahlungsempfaenger(String zahlungsempfaenger) {
        this.zahlungsempfaenger = zahlungsempfaenger;
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

    public int getHashCode() {
        return hashCode;
    }

    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }
  
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode(this.konto);
        hash = 71 * hash + Objects.hashCode(this.datum);
        hash = 71 * hash + Objects.hashCode(this.buchungsText);
        hash = 71 * hash + Objects.hashCode(this.betrag);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HaushaltsbuchRecord other = (HaushaltsbuchRecord) obj;
        if (!Objects.equals(this.konto, other.konto)) {
            return false;
        }
        if (!Objects.equals(this.datum, other.datum)) {
            return false;
        }
        if (!Objects.equals(this.buchungsText, other.buchungsText)) {
            return false;
        }
        if (!Objects.equals(this.betrag, other.betrag)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HaushaltsbuchRecord{" + "konto=" + konto + ", datum=" + datum + ", buchungsText=" + buchungsText + ", hauptkostenArt=" + hauptkostenArt + ", unterkostenArt=" + unterkostenArt + ", verwendungszweck=" + verwendungszweck + ", zahlungsempfaenger=" + zahlungsempfaenger + ", kontonummer=" + kontonummer + ", blz=" + blz + ", betrag=" + betrag + ", hashCode=" + hashCode + '}';
    }
    
  
   
     
}
