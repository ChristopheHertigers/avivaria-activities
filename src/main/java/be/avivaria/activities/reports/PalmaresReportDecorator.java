package be.avivaria.activities.reports;

import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;
import be.indigosolutions.framework.util.StringUtils;

/**
 * User: christophe
 * Date: 03/11/13
 */
public class PalmaresReportDecorator {
    private Hok hok;

    public PalmaresReportDecorator(Hok hok) {
        this.hok = hok;
    }

    private InschrijvingLijn getLijn() {
        return hok.getInschrijvingLijn();
    }

    private InschrijvingHeader getInschrijving() {
        return hok.getInschrijvingLijn().getInschrijving();
    }

    public String getSoort() {
        return getLijn().getRas().getSoort().getNaam();
    }

    public String getRas() {
        return getLijn().getRas().getNaam();
    }

    public String getKleur() {
        return getLijn().getKleur().getNaam();
    }

    public String getGeslachtEnLeeftijd() {
        return hok.getAantal().getNaam() + " " + getLijn().getLeeftijd();
    }

    public Long getHokNummer() {
        return hok.getHoknummer();
    }

    public String getInschrijver() {
        return getInschrijving().getDeelnemer().getNaam();
    }

    public String getPredicaat() {
        String predicaat = hok.getPredicaat();
        if (StringUtils.isNotBlank(predicaat) && StringUtils.containsOnlyDecimalsAnd(predicaat, '/')) {
            predicaat = predicaat.replace(".",",");
        }
        return predicaat;
    }

    public String getTeKoop() {
        return StringUtils.isNotBlank(hok.getPrijs()) ? "X" : "";
    }
}
