package be.avivaria.activities.reports;

import be.avivaria.activities.model.Vereniging;

/**
 * User: christophe
 * Date: 03/11/13
 */
@SuppressWarnings("unused")
public class VerenigingReportDecorator {

    private final Vereniging vereniging;
    private final long aantalDeelnemers;
    private final long aantalDieren;


    public VerenigingReportDecorator(Vereniging vereniging, long aantalDeelnemers, long aantalDieren) {
        this.vereniging = vereniging;
        this.aantalDeelnemers = aantalDeelnemers;
        this.aantalDieren = aantalDieren;
    }

    public String getVereniging() {
        return vereniging.getNaam();
    }

    public long getAantalDeelnemers() {
        return aantalDeelnemers;
    }

    public long getAantalDieren() {
        return aantalDieren;
    }
}
