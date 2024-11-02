package be.avivaria.activities.model;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:03
 */
@Entity
@Table(name="ras_kleur")
public class RasKleur extends BaseEntity {
    @ManyToOne
    private Ras ras;
    @ManyToOne
    private Kleur kleur;
    @Column @Type(type = "be.indigosolutions.framework.dao.BooleanStringType")
    private Boolean erkend;

    public RasKleur() {
    }

    public RasKleur(Ras ras, Kleur kleur, Boolean erkend) {
        this.ras = ras;
        this.kleur = kleur;
        this.erkend = erkend;
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

    public Boolean getErkend() {
        return erkend;
    }

    public void setErkend(Boolean erkend) {
        this.erkend = erkend;
    }
}
