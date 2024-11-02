package be.avivaria.activities.reports;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Ras;
import be.indigosolutions.framework.util.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 02/11/13
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class DeelnemerReportDecorator {
    private Deelnemer deelnemer;
    private List<Ras> rassen;

    public DeelnemerReportDecorator(Deelnemer deelnemer, List<Ras> rassen) {
        this.deelnemer = deelnemer;
        this.rassen = rassen;
    }

    public String getNaam() {
        String naam = deelnemer.getNaam();
        if (StringUtils.isEmpty(naam)) return "";
        return naam.toUpperCase();
    }

    public String getStraat() {
        return deelnemer.getStraat();
    }

    public String getWoonplaats() {
        return deelnemer.getWoonplaats();
    }

    public String getAddress() {
        StringBuilder b = new StringBuilder();
        if (StringUtils.isNotEmpty(getNaam())) b.append(getStraat());
        if (StringUtils.isEmpty(getWoonplaats())) return b.toString();
        if (b.length() > 0) b.append(", ");
        return b.append(getWoonplaats()).toString();
    }

    public String getTelefoon() {
        return deelnemer.getTelefoon();
    }

    public String getFokkerskaartNummer() {
        return deelnemer.getFokkerskaartNummer();
    }

    public Integer getJeugddeelnemer() {
        return deelnemer.getJeugddeelnemer();
    }

    public String getVerenigingNaam() {
        if (deelnemer.getVereniging() == null) return "";
        return deelnemer.getVereniging().getNaam();
    }

    public String getRasListAsString() {
        StringBuilder b = new StringBuilder();
        if (CollectionUtils.isEmpty(rassen)) return b.toString();
        for (Ras ras : rassen) {
            if (b.length() > 0) b.append(", ");
            b.append(ras.getNaam());
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return getNaam();
    }
}
