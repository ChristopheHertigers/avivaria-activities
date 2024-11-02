package be.avivaria.activities.reports;

public class AantalDierenReportDecorator {

    private final String naam;

    private final int totaal;

    public AantalDierenReportDecorator(String naam, Long totaal) {
        this.naam = naam;
        this.totaal = totaal == null ? 0 : totaal.intValue();
    }

    public String getNaam() {
        return naam;
    }

    public int getTotaal() {
        return totaal;
    }
}
