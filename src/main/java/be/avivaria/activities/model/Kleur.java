package be.avivaria.activities.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@Entity
@Table(name="kleur")
public class Kleur extends BaseEntity {
    @Column(length = 50)
    private String naam;

    public Kleur() {
    }

    public Kleur(String naam) {
        this.naam = naam;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    @Override
    public String toString() {
        return naam;
    }
}
