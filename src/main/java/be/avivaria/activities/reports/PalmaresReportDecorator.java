package be.avivaria.activities.reports;

import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;
import be.avivaria.activities.model.Keurmeester;
import be.indigosolutions.framework.util.StringUtils;

/**
 * User: christophe
 * Date: 03/11/13
 */
@SuppressWarnings("unused")
public class PalmaresReportDecorator {
    private final Hok hok;
    private final String rasKeurmeester;
    private final String kleurKeurmeester;

    private String addBreak;

    public PalmaresReportDecorator(Hok hok, String rasKeurmeester, String kleurKeurmeester, String addBreak) {
        this.hok = hok;
        this.rasKeurmeester = rasKeurmeester;
        this.kleurKeurmeester = kleurKeurmeester;
        this.addBreak = addBreak;
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

    public String getRasKeurmeester() {
        return rasKeurmeester;
    }

    public String getKleurKeurmeester() {
        return kleurKeurmeester;
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

    public String getAddBreak() {
        return StringUtils.isNotBlank(addBreak) ? "true" : "false";
    }

    public void setAddBreak(String addBreak) {
        this.addBreak = addBreak;
    }
}
