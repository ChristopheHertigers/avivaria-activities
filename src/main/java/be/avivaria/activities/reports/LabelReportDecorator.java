package be.avivaria.activities.reports;

import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;

/**
 * User: christophe
 * Date: 03/11/13
 */
@SuppressWarnings("unused")
public class LabelReportDecorator {

    private final Hok hok;

    public LabelReportDecorator(Hok hok) {
        this.hok = hok;
    }

    private InschrijvingLijn getLijn() {
        return hok.getInschrijvingLijn();
    }

    private InschrijvingHeader getInschrijving() {
        return hok.getInschrijvingLijn().getInschrijving();
    }

    public Long getHoknummer() {
        return hok.getHoknummer();
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

    public String getPrijs() {
        return hok.getPrijs();
    }

    public String getOpmerking() {
        return hok.getOpmerking();
    }

    public Long getVolgnummer() {
        return getInschrijving().getVolgnummer();
    }
}
