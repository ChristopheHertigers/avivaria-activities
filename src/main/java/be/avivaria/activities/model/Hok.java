package be.avivaria.activities.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@Entity
@Table(name="hok")
public class Hok extends BaseEntity {
    @Column
    private Long hoknummer;
    @Column(length = 50)
    private String prijs;
    @Column(length = 50)
    private String opmerking;
    @ManyToOne
    @JoinColumn(name="inschrijving_lijn_id", nullable = false)
    private InschrijvingLijn inschrijvingLijn;
    @ManyToOne
    private Aantal aantal;
    @Column @Type(type = "be.avivaria.activities.model.usertype.HokTypeUserType")
    private HokType type;
    @Column
    private String predicaat;

    public Hok() {
    }

    public Hok(Long hoknummer, String prijs, String opmerking, InschrijvingLijn inschrijvingLijn, Aantal aantal, HokType type, String predicaat) {
        this.hoknummer = hoknummer;
        this.prijs = prijs;
        this.opmerking = opmerking;
        this.inschrijvingLijn = inschrijvingLijn;
        this.aantal = aantal;
        this.type = type;
        this.predicaat = predicaat;
    }

    public Long getHoknummer() {
        return hoknummer;
    }

    public void setHoknummer(Long hoknummer) {
        this.hoknummer = hoknummer;
    }

    public String getPrijs() {
        return prijs;
    }

    public void setPrijs(String prijs) {
        this.prijs = prijs;
    }

    public String getOpmerking() {
        return opmerking;
    }

    public void setOpmerking(String opmerking) {
        this.opmerking = opmerking;
    }

    public InschrijvingLijn getInschrijvingLijn() {
        return inschrijvingLijn;
    }

    public void setInschrijvingLijn(InschrijvingLijn inschrijvingLijn) {
        this.inschrijvingLijn = inschrijvingLijn;
    }

    public Aantal getAantal() {
        return aantal;
    }

    public void setAantal(Aantal aantal) {
        this.aantal = aantal;
    }

    public HokType getType() {
        return type;
    }

    public void setType(HokType type) {
        this.type = type;
    }

    public String getPredicaat() {
        return predicaat;
    }

    public void setPredicaat(String predicaat) {
        this.predicaat = predicaat;
    }
}
