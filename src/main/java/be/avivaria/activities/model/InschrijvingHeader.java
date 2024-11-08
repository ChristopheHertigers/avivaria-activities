package be.avivaria.activities.model;

import be.indigosolutions.framework.dao.BooleanToStringConverter;
import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.*;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@SuppressWarnings("unused")
@Entity
@Table(name="inschrijving_header")
public class InschrijvingHeader extends BaseEntity {
    @ManyToOne
    private Event event;
    @Column
    private Long volgnummer;
    @ManyToOne
    private Deelnemer deelnemer;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean palmares;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean fokkerskaart;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean fokkerskaart2;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean lidgeld;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean lidgeld2;
    @Column(name="lid_avivaria")
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean lidAvivaria;

    public InschrijvingHeader() {
    }

    public InschrijvingHeader(Event event, Long volgnummer, Deelnemer deelnemer, Boolean palmares, Boolean fokkerskaart,
                              Boolean fokkerskaart2, Boolean lidgeld, Boolean lidgeld2, Boolean lidAvivaria) {
        this.event = event;
        this.volgnummer = volgnummer;
        this.deelnemer = deelnemer;
        this.palmares = palmares;
        this.fokkerskaart = fokkerskaart;
        this.fokkerskaart2 = fokkerskaart2;
        this.lidgeld = lidgeld;
        this.lidgeld2 = lidgeld2;
        this.lidAvivaria = lidAvivaria;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getVolgnummer() {
        return volgnummer;
    }

    public void setVolgnummer(Long volgnummer) {
        this.volgnummer = volgnummer;
    }

    public Deelnemer getDeelnemer() {
        return deelnemer;
    }

    public void setDeelnemer(Deelnemer deelnemer) {
        this.deelnemer = deelnemer;
    }

    public Boolean getPalmares() {
        return palmares;
    }

    public void setPalmares(Boolean palmares) {
        this.palmares = palmares;
    }

    public Boolean getFokkerskaart() {
        return fokkerskaart;
    }

    public void setFokkerskaart(Boolean fokkerskaart) {
        this.fokkerskaart = fokkerskaart;
    }

    public Boolean getFokkerskaart2() {
        return fokkerskaart2;
    }

    public void setFokkerskaart2(Boolean fokkerskaart2) {
        this.fokkerskaart2 = fokkerskaart2;
    }

    public Boolean getLidgeld() {
        return lidgeld;
    }

    public void setLidgeld(Boolean lidgeld) {
        this.lidgeld = lidgeld;
    }

    public Boolean getLidgeld2() {
        return lidgeld2;
    }

    public void setLidgeld2(Boolean lidgeld2) {
        this.lidgeld2 = lidgeld2;
    }

    public Boolean getLidAvivaria() {
        return lidAvivaria;
    }

    public void setLidAvivaria(Boolean lidAvivaria) {
        this.lidAvivaria = lidAvivaria;
    }
}
