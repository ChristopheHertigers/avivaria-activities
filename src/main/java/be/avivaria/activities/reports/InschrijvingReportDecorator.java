package be.avivaria.activities.reports;

import be.avivaria.activities.model.*;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * User: christophe
 * Date: 02/11/13
 */
@SuppressWarnings("unused")
public class InschrijvingReportDecorator {
    private final Long aantalDieren;
    private final Long aantalTeKoop;
    private final Hok hok;

    public InschrijvingReportDecorator(Hok hok, long aantalDieren, long aantalTeKoop) {
        this.hok = hok;
        this.aantalDieren = aantalDieren;
        this.aantalTeKoop = aantalTeKoop;
    }

    private InschrijvingLijn getLijn() {
        return hok.getInschrijvingLijn();
    }

    private InschrijvingHeader getInschrijving() {
        return hok.getInschrijvingLijn().getInschrijving();
    }

    private Deelnemer getDeelnemer() {
        return hok.getInschrijvingLijn().getInschrijving().getDeelnemer();
    }

    private Event getEvent() {
        return hok.getInschrijvingLijn().getInschrijving().getEvent();
    }

    public long getInschrijvingId() {
        return getInschrijving().getId();
    }

    public Long getInschrijvingVolgnummer() {
        return getInschrijving().getVolgnummer();
    }

    public Long getHoknummer() {
        return hok.getHoknummer();
    }

    public String getAantal() {
        return hok.getAantal().getNaam() + " " + getLijn().getLeeftijd();
    }

    public String getRas() {
        return getLijn().getRas().getNaam();
    }

    public String getSoort() {
        return getLijn().getRas().getSoort().getAfkorting();
    }

    public String getKleur() {
        return getLijn().getKleur().getNaam();
    }

    public String getRingNummer() {
        if (StringUtils.isEmpty(getLijn().getRingnummer())) return "";
        return getLijn().getRingnummer();
    }

    public String getPrijs() {
        return hok.getPrijs();
    }

    public String getDeelnemerNaam() {
        return getDeelnemer().getNaam();
    }

    public String getDeelnemerStraat() {
        return getDeelnemer().getStraat();
    }

    public String getDeelnemerWoonplaats() {
        return getDeelnemer().getWoonplaats();
    }

    public String getDeelnemerTelefoon() {
        return getDeelnemer().getTelefoon();
    }

    public String getDeelnemerVereniging() {
        if (getDeelnemer().getVereniging() == null) return "";
        return getDeelnemer().getVereniging().getNaam();
    }

    public String getDeelnemerFokkerskaartNummer() {
        return getDeelnemer().getFokkerskaartNummer();
    }

    public String getJeugdDeelnemer() {
        Integer jeugd = getDeelnemer().getJeugddeelnemer();
        return jeugd != null && jeugd >= (getCurrentYear()-18) ? "X" : "";
    }

    public Long getAantalDieren() {
        return aantalDieren;
    }

    public Integer getPalmares() {
        return getInschrijving().getPalmares() ? 1 : 0;
    }

    public Long getAantalTeKoop() {
        return aantalTeKoop;
    }

    public Integer getLidgeld() {
        return getInschrijving().getLidgeld() ? 1 : 0;
    }

    public Integer getLidgeldJeugd() {
        return getInschrijving().getLidgeld2() ? 1 : 0;
    }

    public Integer getFokkerskaart() {
        return getInschrijving().getFokkerskaart() ? 1 : 0;
    }

    public Integer getFokkerskaart2() {
        return getInschrijving().getFokkerskaart2() ? 1 : 0;
    }

    private Double getPrijsDierDouble() {
        return getEvent().getPrijsDier();
    }

    private Double getPrijsDierClubDouble() {
        return getEvent().getPrijsDierClub();
    }

    private Double getPrijsDierForDeelnemer() {
        if (getInschrijving().getLidAvivaria() || getDeelnemer().getVereniging() != null && ID_AVIVARIA == getDeelnemer().getVereniging().getId())
            return getPrijsDierClubDouble();
        return getPrijsDierDouble();
    }

    private Double getPrijsPalmaresDouble() {
        return getEvent().getPrijsPalmares();
    }

    private Double getPrijsDierTeKoopDouble() {
        return getEvent().getPrijsDierTeKoop();
    }

    private Double getPrijsLidgeldDouble() {
        return getEvent().getPrijsLidgeld();
    }

    private Double getPrijsLidgeldJeugdDouble() {
        return getEvent().getPrijsLidgeldJeugd();
    }

    private Double getPrijsFokkerskaartDouble() {
        return getEvent().getPrijsFokkerskaart();
    }

    private Double getPrijsFokkerskaart2Double() {
        return getEvent().getPrijsFokkerskaart2();
    }

    private static final NumberFormat FORMAT = DecimalFormat.getInstance(new Locale("nl","BE"));
    static {
        FORMAT.setMinimumFractionDigits(2);
    }
    private static final long ID_AVIVARIA = 1;

    public String getPrijsDier() {
        return "x " + FORMAT.format(getPrijsDierForDeelnemer()) + " =";
    }

    public String getPrijsPalmares() {
        return "x " + FORMAT.format(getPrijsPalmaresDouble()) + " =";
    }

    public String getPrijsDierTeKoop() {
        return "x " + FORMAT.format(getPrijsDierTeKoopDouble()) + " =";
    }

    public String getPrijsLidgeld() {
        return "x " + FORMAT.format(getPrijsLidgeldDouble()) + " =";
    }

    public String getPrijsLidgeldJeugd() {
        return "x " + FORMAT.format(getPrijsLidgeldJeugdDouble()) + " =";
    }

    public String getPrijsFokkerskaart() {
        return "x " + FORMAT.format(getPrijsFokkerskaartDouble()) + " =";
    }

    public String getPrijsFokkerskaart2() {
        return "x " + FORMAT.format(getPrijsFokkerskaart2Double()) + " =";
    }

    public String getTotaalDier() {
        return FORMAT.format(getPrijsDierForDeelnemer() * getAantalDieren());
    }

    public String getTotaalPalmares() {
        return FORMAT.format(getPrijsPalmaresDouble() * getPalmares());
    }

    public String getTotaalDierTeKoop() {
        return FORMAT.format(getPrijsDierTeKoopDouble() * getAantalTeKoop());
    }

    public String getTotaalLidgeld() {
        return FORMAT.format(getPrijsLidgeldDouble() * getLidgeld());
    }

    public String getTotaalLidgeldJeugd() {
        return FORMAT.format(getPrijsLidgeldJeugdDouble() * getLidgeldJeugd());
    }

    public String getTotaalFokkerskaart() {
        return FORMAT.format(getPrijsFokkerskaartDouble() * getFokkerskaart());
    }

    public String getTotaalFokkerskaart2() {
        return FORMAT.format(getPrijsFokkerskaart2Double() * getFokkerskaart2());
    }

    public String getTotaal() {
        return FORMAT.format((getPrijsDierForDeelnemer() * getAantalDieren()) +
                (getPrijsPalmaresDouble() * getPalmares()) +
                (getPrijsDierTeKoopDouble() * getAantalTeKoop()) +
                (getPrijsLidgeldDouble() * getLidgeld()) +
                (getPrijsLidgeldJeugdDouble() * getLidgeldJeugd()) +
                (getPrijsFokkerskaartDouble() * getFokkerskaart()) +
                (getPrijsFokkerskaart2Double() * getFokkerskaart2()));
    }

    private static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

}
