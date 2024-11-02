package be.avivaria.activities.model;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@Entity
@Table(name="deelnemer")
public class Deelnemer extends BaseEntity {
    @Column(length = 50)
    private String naam;
    @Column(length = 100)
    private String straat;
    @Column(length = 100)
    private String woonplaats;
    @Column(length = 50)
    private String telefoon;
    @Column(name="fokkerskaart_nummer", length = 20)
    private String fokkerskaartNummer;
    @Column(length = 50)
    private Integer jeugddeelnemer;
    @ManyToOne
    private Vereniging vereniging;

    public Deelnemer() {
    }

    public Deelnemer(String naam, String straat, String woonplaats, String telefoon, String fokkerskaartNummer, Integer jeugddeelnemer, Vereniging vereniging) {
        this.naam = naam;
        this.straat = straat;
        this.woonplaats = woonplaats;
        this.telefoon = telefoon;
        this.fokkerskaartNummer = fokkerskaartNummer;
        this.jeugddeelnemer = jeugddeelnemer;
        this.vereniging = vereniging;
    }

    public String getNaam() {
        return naam;
    }

    public String getNaamForReport() {
        if (StringUtils.isEmpty(naam)) return "";
        return naam.toUpperCase();
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }

    public String getAddressForReport() {
        StringBuilder b = new StringBuilder();
        if (StringUtils.isNotEmpty(straat)) b.append(straat);
        if (StringUtils.isEmpty(woonplaats)) return b.toString();
        if (b.length() > 0) b.append(", ");
        return b.append(woonplaats).toString();
    }

    public String getTelefoon() {
        return telefoon;
    }

    public void setTelefoon(String telefoon) {
        this.telefoon = telefoon;
    }

    public String getFokkerskaartNummer() {
        return fokkerskaartNummer;
    }

    public void setFokkerskaartNummer(String fokkerskaartNummer) {
        this.fokkerskaartNummer = fokkerskaartNummer;
    }

    public Integer getJeugddeelnemer() {
        return jeugddeelnemer;
    }

    public void setJeugddeelnemer(Integer jeugddeelnemer) {
        this.jeugddeelnemer = jeugddeelnemer;
    }

    public Vereniging getVereniging() {
        return vereniging;
    }

    public void setVereniging(Vereniging vereniging) {
        this.vereniging = vereniging;
    }

    @Override
    public String toString() {
        return getNaam();
    }
}
