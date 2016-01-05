package de.bimalo.haushaltsbuch.transformator;

/**
 * <p>
 * Represents a single records in a CSV file provided by Comdirect.</p>
 *
 * @author <a href="mailto:markus.lohn@bimalo.de">Markus Lohn</a>
 * @version $Rev$ $LastChangedDate$
 * @since 1.0
 */
public final class ComdirectRecord {

    private String buchungstag;
    private String vorgang;
    private String buchungstext;
    private String umsatz;

    public String getBuchungstag() {
        return buchungstag;
    }

    public void setBuchungstag(String buchungstag) {
        this.buchungstag = buchungstag;
    }

    public String getVorgang() {
        return vorgang;
    }

    public void setVorgang(String vorgang) {
        this.vorgang = vorgang;
    }

    public String getBuchungstext() {
        return buchungstext;
    }

    public void setBuchungstext(String buchungstext) {
        this.buchungstext = buchungstext;
    }

    public String getUmsatz() {
        return umsatz;
    }

    public void setUmsatz(String umsatz) {
        this.umsatz = umsatz;
    }

}
