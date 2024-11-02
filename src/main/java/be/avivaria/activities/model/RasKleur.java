package be.avivaria.activities.model;

import be.indigosolutions.framework.dao.BooleanToStringConverter;
import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.*;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:03
 */
@SuppressWarnings("unused")
@Entity
@Table(name="ras_kleur")
public class RasKleur extends BaseEntity {
    @ManyToOne
    private Ras ras;
    @ManyToOne
    private Kleur kleur;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
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
