package be.avivaria.activities.model;

import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * User: christophe
 * Date: 11/11/13
 */
@Entity
@Table(name = "keurmeester")
public class Keurmeester extends BaseEntity implements Cloneable {
    @Column(length = 5)
    private String naam;
    @ManyToOne
    private Event event;
    @ManyToOne
    private Ras ras;
    @ManyToOne
    private Kleur kleur;

    public Keurmeester() {
    }

    public Keurmeester(long id, String naam, Event event, Ras ras, Kleur kleur) {
        super(id);
        this.naam = naam;
        this.event = event;
        this.ras = ras;
        this.kleur = kleur;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Keurmeester clone = (Keurmeester) super.clone();
        clone.setId(this.id);
        clone.setNaam(this.naam);
        clone.setEvent(this.event);
        clone.setRas(this.ras);
        clone.setKleur(this.kleur);
        return clone;
    }
}
