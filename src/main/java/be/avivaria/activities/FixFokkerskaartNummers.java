package be.avivaria.activities;

import be.avivaria.activities.dao.DeelnemerDao;
import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.reports.ReportDataFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import java.util.Calendar;
import java.util.List;

/**
 * User: christophe
 * Date: 03/11/13
 */
public class FixFokkerskaartNummers {

    public static void main(String[] args) {
        Session session = ReportDataFactory.getPersistenceContext();
        try {

            DeelnemerDao dao = new DeelnemerDao(session);
            List<Deelnemer> deelnemers = dao.findByEventId(22);
            for (Deelnemer deelnemer : deelnemers) {

                String oudFokkerskaartNr = deelnemer.getFokkerskaartNummer();
                if (StringUtils.isNotEmpty(oudFokkerskaartNr)) {
                    String noSpaces = oudFokkerskaartNr.replaceAll("\\s", "");

                    if (!StringUtils.isNumeric(noSpaces)) {
                        System.out.println(pad(oudFokkerskaartNr) + " => NOT NUMERIC, ABORT (" + deelnemer.getId() + ", " + deelnemer.getNaam() + ")");
                        continue;
                    }

                    if (noSpaces.length() != 10) {
                        System.out.println(pad(oudFokkerskaartNr) + " => " + noSpaces + " INVALID LENGTH, ABORT (" + deelnemer.getId() + ", " + deelnemer.getNaam() + ")");
                        continue;
                    }

                    String nieuwFokkerskaartNr = (getCurrentYear()-2000) + " " + noSpaces.substring(2,3) + " " + noSpaces.substring(3,6) + " " + noSpaces.substring(6);

                    System.out.println(pad(oudFokkerskaartNr) + " => " + nieuwFokkerskaartNr);

                    deelnemer.setFokkerskaartNummer(nieuwFokkerskaartNr);
                    dao.saveOrUpdate(deelnemer);
                }
            }
            dao.flush();

        } finally {
            session.close();
        }
    }

    private static String pad(String value) {
        if (value == null) return "             ";
        if (value.length() >= 13) return value;
        StringBuilder b = new StringBuilder(value);
        while (b.length() < 13) {
              b.append(" ");
        }
        return b.toString();
    }

    private static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }


}
