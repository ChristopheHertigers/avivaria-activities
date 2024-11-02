package be.avivaria.activities.model;

import be.avivaria.activities.model.usertype.EventTypeUserType;
import be.indigosolutions.framework.dao.BooleanToStringConverter;
import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Type;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:02
 */
@SuppressWarnings("unused")
@Entity
@Table(name="event")
public class Event extends BaseEntity {
    @Column(length = 50) @Type(EventTypeUserType.class)
    private EventType type;
    @Column(length = 100)
    private String naam;
    @Column(name="dieren")
    private Double prijsDier;
    @Column(name="dieren_club")
    private Double prijsDierClub;
    @Column(name="dieren_te_koop")
    private Double prijsDierTeKoop;
    @Column(name="palmares")
    private Double prijsPalmares;
    @Column(name="lidgeld")
    private Double prijsLidgeld;
    @Column(name="lidgeld_jeugd")
    private Double prijsLidgeldJeugd;
    @Column(name="fokkerskaart")
    private Double prijsFokkerskaart;
    @Column(name="fokkerskaart_2")
    private Double prijsFokkerskaart2;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean selected;
    @Column(name="club_naam_regel_1")
    private String clubNaamRegel1;
    @Column(name="club_naam_regel_2")
    private String clubNaamRegel2;
    @Column(name="hok_start_nummer")
    private Integer hokStartNummer;

    public Event() {
    }

    public Event(EventType type, String naam, Double prijsDier, Double prijsDierClub, Double prijsDierTeKoop, Double prijsPalmares, Double prijsLidgeld, Double prijsLidgeldJeugd, Double prijsFokkerskaart, Double prijsFokkerskaart2, Boolean selected, String clubNaamRegel1, String clubNaamRegel2, Integer hokStartNummer) {
        this.type = type;
        this.naam = naam;
        this.prijsDier = prijsDier;
        this.prijsDierClub = prijsDierClub;
        this.prijsDierTeKoop = prijsDierTeKoop;
        this.prijsPalmares = prijsPalmares;
        this.prijsLidgeld = prijsLidgeld;
        this.prijsLidgeldJeugd = prijsLidgeldJeugd;
        this.prijsFokkerskaart = prijsFokkerskaart;
        this.prijsFokkerskaart2 = prijsFokkerskaart2;
        this.selected = selected;
        this.clubNaamRegel1 = clubNaamRegel1;
        this.clubNaamRegel2 = clubNaamRegel2;
        this.hokStartNummer = hokStartNummer;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Double getPrijsDier() {
        return prijsDier;
    }

    public void setPrijsDier(Double prijsDier) {
        this.prijsDier = prijsDier;
    }

    public Double getPrijsDierClub() {
        return prijsDierClub;
    }

    public void setPrijsDierClub(Double prijsDierClub) {
        this.prijsDierClub = prijsDierClub;
    }

    public Double getPrijsDierTeKoop() {
        return prijsDierTeKoop;
    }

    public void setPrijsDierTeKoop(Double prijsDierTeKoop) {
        this.prijsDierTeKoop = prijsDierTeKoop;
    }

    public Double getPrijsPalmares() {
        return prijsPalmares;
    }

    public void setPrijsPalmares(Double prijsPalmares) {
        this.prijsPalmares = prijsPalmares;
    }

    public Double getPrijsLidgeld() {
        return prijsLidgeld;
    }

    public void setPrijsLidgeld(Double prijsLidgeld) {
        this.prijsLidgeld = prijsLidgeld;
    }

    public Double getPrijsLidgeldJeugd() {
        return prijsLidgeldJeugd;
    }

    public void setPrijsLidgeldJeugd(Double prijsLidgeldJeugd) {
        this.prijsLidgeldJeugd = prijsLidgeldJeugd;
    }

    public Double getPrijsFokkerskaart() {
        return prijsFokkerskaart;
    }

    public void setPrijsFokkerskaart(Double prijsFokkerskaart) {
        this.prijsFokkerskaart = prijsFokkerskaart;
    }

    public Double getPrijsFokkerskaart2() {
        return prijsFokkerskaart2;
    }

    public void setPrijsFokkerskaart2(Double prijsFokkerskaart2) {
        this.prijsFokkerskaart2 = prijsFokkerskaart2;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getClubNaamRegel1() {
        return clubNaamRegel1;
    }

    public void setClubNaamRegel1(String clubNaamRegel1) {
        this.clubNaamRegel1 = clubNaamRegel1;
    }

    public String getClubNaamRegel2() {
        return clubNaamRegel2;
    }

    public void setClubNaamRegel2(String clubNaamRegel2) {
        this.clubNaamRegel2 = clubNaamRegel2;
    }

    public Integer getHokStartNummer() {
        return hokStartNummer == null ? 1 : hokStartNummer;
    }

    public void setHokStartNummer(Integer hokStartNummer) {
        this.hokStartNummer = hokStartNummer;
    }
}
