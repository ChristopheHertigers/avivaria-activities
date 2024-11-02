package be.avivaria.activities.model;

import be.avivaria.activities.model.usertype.HokTypeUserType;
import be.indigosolutions.framework.dao.BooleanToStringConverter;
import be.indigosolutions.framework.model.BaseEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:03
 */
@SuppressWarnings("unused")
@Entity
@Table(name = "ras")
public class Ras extends BaseEntity {
    @ManyToOne
    private Soort soort;
    @Column(length = 100)
    private String naam;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private Boolean erkend;
    @Column(name = "hoktype_man") @Type(HokTypeUserType.class)
    private HokType hokTypeMan;
    @Column(name = "hoktype_vrouw") @Type(HokTypeUserType.class)
    private HokType hokTypeVrouw;
    @Column
    @Convert(converter= BooleanToStringConverter.class)
    private boolean belgisch;
    @Column
    private Integer volgorde;

    public Ras() {
    }

    public Ras(Soort soort, String naam, Boolean erkend, HokType hokTypeMan, HokType hokTypeVrouw, boolean belgisch, Integer volgorde) {
        this.soort = soort;
        this.naam = naam;
        this.erkend = erkend;
        this.hokTypeMan = hokTypeMan;
        this.hokTypeVrouw = hokTypeVrouw;
        this.belgisch = belgisch;
        this.volgorde = volgorde;
    }

    public Soort getSoort() {
        return soort;
    }

    public void setSoort(Soort soort) {
        this.soort = soort;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Boolean getErkend() {
        return erkend;
    }

    public void setErkend(Boolean erkend) {
        this.erkend = erkend;
    }

    public HokType getHokTypeMan() {
        return hokTypeMan;
    }

    public void setHokTypeMan(HokType hokType) {
        this.hokTypeMan = hokType;
    }

    public HokType getHokTypeVrouw() {
        return hokTypeVrouw;
    }

    public void setHokTypeVrouw(HokType hokTypeVrouw) {
        this.hokTypeVrouw = hokTypeVrouw;
    }

    public HokType getSmallestHokType() {
        return getHokTypeMan().getType() < getHokTypeVrouw().getType() ? getHokTypeMan() : getHokTypeVrouw();
    }

    public boolean isBelgisch() {
        return belgisch;
    }

    public void setBelgisch(boolean belgisch) {
        this.belgisch = belgisch;
    }

    public Integer getVolgorde() {
        return volgorde;
    }

    public void setVolgorde(Integer volgorde) {
        this.volgorde = volgorde;
    }

    @Override
    public String toString() {
        return naam + " (" + getSoort().getNaam() + ")";
    }
}
