package be.avivaria.activities.model;

import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:03
 */
@Entity
@Table(name="soort")
public class Soort extends BaseEntity {
    @Column(length = 2)
    private String afkorting;
    @Column(length = 50)
    private String naam;

    public Soort() {
    }

    public Soort(String afkorting, String naam) {
        this.afkorting = afkorting;
        this.naam = naam;
    }

    public String getAfkorting() {
        return afkorting;
    }

    public void setAfkorting(String afkorting) {
        this.afkorting = afkorting;
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
