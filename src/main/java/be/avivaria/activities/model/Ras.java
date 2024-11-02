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
@Table(name = "ras")
public class Ras extends BaseEntity {
    @ManyToOne
    private Soort soort;
    @Column(length = 100)
    private String naam;
    @Column @Type(type = "be.indigosolutions.framework.dao.BooleanStringType")
    private Boolean erkend;
    @Column(name = "hoktype_man") @Type(type = "be.avivaria.activities.model.usertype.HokTypeUserType")
    private HokType hokTypeMan;
    @Column(name = "hoktype_vrouw") @Type(type = "be.avivaria.activities.model.usertype.HokTypeUserType")
    private HokType hokTypeVrouw;
    @Column @Type(type = "be.indigosolutions.framework.dao.BooleanStringType")
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
