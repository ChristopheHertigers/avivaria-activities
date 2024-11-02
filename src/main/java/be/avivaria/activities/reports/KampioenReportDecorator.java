package be.avivaria.activities.reports;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.HoofdSoort;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * User: christophe
 * Date: 05/11/13
 */
public class KampioenReportDecorator implements Comparable<KampioenReportDecorator> {

    private HoofdSoort hoofdSoort;
    private int order = 0;
    private Deelnemer deelnemer;
    private double totaal;
    private int totaalJong;
    private int totaalMan;
    private boolean lidAvivaria;

    public KampioenReportDecorator(HoofdSoort hoofdSoort, Deelnemer deelnemer, double totaal, int totaalJong, int totaalMan, boolean lidAvivaria) {
        this.hoofdSoort = hoofdSoort;
        this.deelnemer = deelnemer;
        this.totaal = totaal;
        this.totaalJong = totaalJong;
        this.totaalMan = totaalMan;
        this.lidAvivaria = lidAvivaria;
    }

    public HoofdSoort getHoofdSoort() {
        return hoofdSoort;
    }

    public String getOrder() {
        return order+".";
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getNaam() {
        return deelnemer.getNaam();
    }

    private static final NumberFormat FORMAT = DecimalFormat.getInstance(new Locale("nl", "BE"));
    public String getTotaal() {
        return FORMAT.format(totaal);
    }

    double getTotaalValue() {
        return totaal;
    }

    public Integer getTotaalJong() {
        return totaalJong;
    }

    public Integer getTotaalMan() {
        return totaalMan;
    }

    public String getLidAvivaria() {
        return lidAvivaria ? "X" : "";
    }

    @Override
    public int compareTo(KampioenReportDecorator o) {
        int i = getHoofdSoort().compareTo(o.getHoofdSoort());
        if (i != 0) return i;
        i = -1 * getTotaal().compareTo(o.getTotaal());
        if (i != 0) return i;
        i = -1 * getTotaalJong().compareTo(o.getTotaalJong());
        if (i != 0) return i;
        i = -1 * getTotaalMan().compareTo(o.getTotaalMan());
        if (i != 0) return i;
        return getNaam().compareTo(o.getNaam());
    }
}
