package be.avivaria.activities.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: christophe
 * Date: 05/10/13
 * Time: 15:01
 */
@Entity
@Table(name = "aantal")
public class Aantal extends BaseEntity {
    @Column(length = 5)
    private String naam;
    @Column
    private Integer aantal;
    @Column
    private Integer aantal2;
    @Column
    private String benaming;

    public Aantal() {
    }

    public Aantal(long id, String naam, Integer aantal, Integer aantal2, String benaming) {
        super(id);
        this.naam = naam;
        this.aantal = aantal;
        this.aantal2 = aantal2;
        this.benaming = benaming;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Integer getAantal() {
        return aantal;
    }

    public void setAantal(Integer aantal) {
        this.aantal = aantal;
    }

    public Integer getAantal2() {
        return aantal2;
    }

    public void setAantal2(Integer aantal2) {
        this.aantal2 = aantal2;
    }

    public String getBenaming() {
        return benaming;
    }

    public void setBenaming(String benaming) {
        this.benaming = benaming;
    }

    public boolean isMale() {
        return "1-0".equals(naam);
    }

    public boolean isFemale() {
        return "0-1".equals(naam);
    }

    @Override
    public String toString() {
        return naam;
    }
}
