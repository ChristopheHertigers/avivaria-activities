package be.avivaria.activities.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:03
 */
@Entity
@Table(name="vereniging")
public class Vereniging extends BaseEntity {
    @Column(length = 100)
    private String naam;

    public Vereniging() {
    }

    public Vereniging(String naam) {
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
