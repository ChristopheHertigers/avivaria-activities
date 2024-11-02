package be.avivaria.activities.model;

import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@Entity
@Table(name = "inschrijving_lijn")
public class InschrijvingLijn extends BaseEntity implements Cloneable {
    private static final Aantal ONE_MALE = new Aantal(1, "1-0", 1, 1, "");
    private static final Aantal ONE_FEMALE = new Aantal(2, "0-1", 1, 1, "");
    private static final NumberFormat FORMAT = NumberFormat.getCurrencyInstance(new Locale("nl", "BE"));
    @ManyToOne
    @JoinColumn(name = "inschrijving_header_id", nullable = false)
    private InschrijvingHeader inschrijving;
    @ManyToOne
    private Ras ras;
    @ManyToOne
    private Kleur kleur;
    @Column(length = 5)
    private String leeftijd;
    @ManyToOne
    private Aantal aantal;
    @Column(length = 50)
    private String ringnummer;
    @Column
    private Double prijs;

    public InschrijvingLijn() {
    }

    public InschrijvingLijn(InschrijvingHeader inschrijving, Ras ras, Kleur kleur, String leeftijd, Aantal aantal, String ringnummer, Double prijs) {
        this.inschrijving = inschrijving;
        this.ras = ras;
        this.kleur = kleur;
        this.leeftijd = leeftijd;
        this.aantal = aantal;
        this.ringnummer = ringnummer;
        this.prijs = prijs;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        InschrijvingLijn clone = (InschrijvingLijn) super.clone();
        clone.setId(getId());
        clone.setInschrijving(getInschrijving());
        clone.setRas(getRas());
        clone.setKleur(getKleur());
        clone.setLeeftijd(getLeeftijd());
        clone.setAantal(getAantal());
        clone.setRingnummer(getRingnummer());
        clone.setPrijs(getPrijs());
        return clone;
    }

    public InschrijvingHeader getInschrijving() {
        return inschrijving;
    }

    public void setInschrijving(InschrijvingHeader inschrijving) {
        this.inschrijving = inschrijving;
    }

    public Ras getRas() {
        return ras;
    }

    public void setRas(Ras ras) {
        this.ras = ras;
    }

    public Kleur getKleur() {
        return kleur;
    }

    public void setKleur(Kleur kleur) {
        this.kleur = kleur;
    }

    public String getLeeftijd() {
        return leeftijd;
    }

    public void setLeeftijd(String leeftijd) {
        this.leeftijd = leeftijd;
    }

    public Aantal getAantal() {
        return aantal;
    }

    public void setAantal(Aantal aantal) {
        this.aantal = aantal;
    }

    public String getRingnummer() {
        return ringnummer;
    }

    public void setRingnummer(String ringnummer) {
        this.ringnummer = ringnummer;
    }

    public Double getPrijs() {
        return prijs;
    }

    public void setPrijs(Double prijs) {
        this.prijs = prijs;
    }

    public List<Hok> getHokList() {
        List<Hok> hokken = new ArrayList<>();
        String prijs = getFormattedPrijs();
        if (EventType.Tentoonstelling == inschrijving.getEvent().getType()) {
            // split into seperate hokken
            String[] aantalParts = getAantal().getNaam().split("-");
            int males = Integer.parseInt(aantalParts[0]);
            int females = Integer.parseInt(aantalParts[1]);
            for (int i = 0; i < males; i++) {
                hokken.add(new Hok(0L, prijs, "", this, ONE_MALE, getRas().getHokTypeMan(), null));
            }
            for (int i = 0; i < females; i++) {
                hokken.add(new Hok(0L, prijs, "", this, ONE_FEMALE, getRas().getHokTypeVrouw(), null));
            }
        } else {
            // don't split
            // todo hoktype grootste nemen?
            hokken.add(new Hok(0L, prijs, "", this, getAantal(), getRas().getHokTypeMan(), null));
        }
        return hokken;
    }

    private String getFormattedPrijs() {
        if (getPrijs() == null) return "";
        String value = FORMAT.format(getPrijs());
        if (StringUtils.isNotBlank(getAantal().getBenaming())) {
            value += "/" + getAantal().getBenaming();
        }
        return value;
    }
}
