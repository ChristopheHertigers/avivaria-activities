package be.avivaria.activities.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    public Keurmeester() {
    }

    public Keurmeester(long id, String naam, Event event) {
        super(id);
        this.naam = naam;
        this.event = event;
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

    @Override
    public Object clone() throws CloneNotSupportedException {
        Keurmeester clone = (Keurmeester) super.clone();
        clone.setId(this.id);
        clone.setNaam(this.naam);
        clone.setEvent(this.event);
        return clone;
    }
}
