package be.avivaria.activities.reports;

import be.avivaria.activities.dao.*;
import be.avivaria.activities.model.*;
import be.indigosolutions.framework.util.HibernateUtils;
import be.indigosolutions.framework.util.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import java.util.*;
import java.util.function.Predicate;

/**
 * User: christophe
 * Date: 01/11/13
 */
public class ReportDataFactory {

    /**
     * Get a new hibernate session. Note that you are responsible for closing it yourself.
     *
     * @return Session
     */
    public static Session getPersistenceContext() {
        return HibernateUtils.getSessionFactory().openSession();
    }

    public static Event getActiveEvent(Session session) {
        EventDao dao = new EventDao(session);
        return dao.findSelected();
    }

    private static List<DeelnemerReportDecorator> getDeelnemerInfo(Session session, Predicate<Deelnemer> filter) {
        DeelnemerDao dao = new DeelnemerDao(session);
        RasDao rasDao = new RasDao(session);
        Event event = getActiveEvent(session);
        List<Deelnemer> deelnemers = dao.findByEventId(event.getId());
        List<DeelnemerReportDecorator> results = new ArrayList<>();
        Map<Deelnemer, List<Ras>> rassen = rasDao.findRasListPerDeelnemerByEventId(event.getId());
        for (Deelnemer deelnemer : deelnemers) {
            if (filter.test(deelnemer)) {
                results.add(new DeelnemerReportDecorator(deelnemer, rassen.get(deelnemer)));
            }
        }
        return results;
    }

    public static List<DeelnemerReportDecorator> getDeelnemersForActiveEvent(Session session) {
        return getDeelnemerInfo(session, deelnemer -> deelnemer.getJeugddeelnemer() == null || deelnemer.getJeugddeelnemer() < (getCurrentYear()-18));
    }

    public static List<DeelnemerReportDecorator> getJeugdDeelnemersForActiveEvent(Session session) {
        return getDeelnemerInfo(session, deelnemer -> deelnemer.getJeugddeelnemer() != null && deelnemer.getJeugddeelnemer() >= (getCurrentYear()-18));
    }

    public static List<InschrijvingReportDecorator> getInschrijvingenForActiveEvent(Session session) {
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Hok> hokken = hokDao.findByEventIdOrderedByInschrijving(event.getId());
        List<InschrijvingReportDecorator> results = new ArrayList<>();
        for (Hok hok : hokken) {
            long aantalDieren = countDierenForInschrijving(hokken, hok.getInschrijvingLijn().getInschrijving());
            long aantalDierenTeKoop = countDierenTeKoopForInschrijving(hokken, hok.getInschrijvingLijn().getInschrijving());
            results.add(new InschrijvingReportDecorator(hok, aantalDieren, aantalDierenTeKoop));
        }
        return results;
    }

    private static long countDierenForInschrijving(List<Hok> hokken, InschrijvingHeader inschrijving) {
        long aantal = 0L;
        for (Hok hok : hokken) {
            if (hok.getInschrijvingLijn().getInschrijving().getId().equals(inschrijving.getId())) {
                aantal += hok.getAantal().getAantal2();
            }
        }
        return aantal;
    }

    private static long countDierenTeKoopForInschrijving(List<Hok> hokken, InschrijvingHeader inschrijving) {
        long aantal = 0L;
        for (Hok hok : hokken) {
            if (EventType.Tentoonstelling == inschrijving.getEvent().getType()) {
                if (hok.getInschrijvingLijn().getInschrijving().getId().equals(inschrijving.getId())
                        && hok.getInschrijvingLijn().getPrijs() != null
                        && hok.getInschrijvingLijn().getPrijs() > 0) {
                    aantal += hok.getAantal().getAantal2();
                }
            } else if (EventType.Verkoopdag == inschrijving.getEvent().getType()) {
                if (hok.getInschrijvingLijn().getInschrijving().getId().equals(inschrijving.getId())) {
                    aantal++;
                }
            }
        }
        return aantal;
    }

    public static List<PalmaresReportDecorator> getPalmaresForActiveEvent(Session session) {
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Hok> hokken = hokDao.findByEventId(event.getId());
        List<PalmaresReportDecorator> results = new ArrayList<>();
        for (Hok hok : hokken) {
            results.add(new PalmaresReportDecorator(hok));
        }
        return results;
    }

    public static List<VerenigingReportDecorator> getVerenigingenForActiveEvent(Session session) {
        VerenigingDao verenigingDao = new VerenigingDao(session);
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Vereniging> verenigingen = verenigingDao.findByEventId(event.getId());
        List<Hok> hokken = hokDao.findByEventId(event.getId());
        List<VerenigingReportDecorator> results = new ArrayList<>();
        for (Vereniging vereniging : verenigingen) {
            long aantalDeelnemers = countDeelnemersForVereniging(hokken, vereniging);
            long aantalDieren = countDierenForVereniging(hokken, vereniging);
            results.add(new VerenigingReportDecorator(vereniging, aantalDeelnemers, aantalDieren));
        }
        return results;
    }

    private static long countDeelnemersForVereniging(List<Hok> hokken, Vereniging vereniging) {
        long aantal = 0L;
        Set<Long> processedInschrijvingIds = new HashSet<>();
        for (Hok hok : hokken) {
            InschrijvingHeader inschrijving = hok.getInschrijvingLijn().getInschrijving();
            if (!processedInschrijvingIds.contains(inschrijving.getId())) {
                Deelnemer deelnemer = inschrijving.getDeelnemer();
                if (deelnemer.getVereniging() != null && deelnemer.getVereniging().getId().equals(vereniging.getId())) {
                    aantal++;
                }
                processedInschrijvingIds.add(inschrijving.getId());
            }
        }
        return aantal;
    }

    private static long countDierenForVereniging(List<Hok> hokken, Vereniging vereniging) {
        long aantal = 0L;
        for (Hok hok : hokken) {
            Deelnemer deelnemer = hok.getInschrijvingLijn().getInschrijving().getDeelnemer();
            if (deelnemer.getVereniging() != null && deelnemer.getVereniging().getId().equals(vereniging.getId())) {
                aantal += hok.getAantal().getAantal2();
            }
        }
        return aantal;
    }

    public static List<LabelReportDecorator> getHokLabelsForActiveEvent(Session session) {
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Hok> hokken = hokDao.findByEventId(event.getId());
        List<LabelReportDecorator> results = new ArrayList<>();
        for (Hok hok : hokken) {
            results.add(new LabelReportDecorator(hok));
        }
        return results;
    }

    public static List<LabelReportDecorator> getPrijsLabelsForActiveEvent(Session session) {
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Hok> hokken = hokDao.findByEventId(event.getId());
        List<LabelReportDecorator> results = new ArrayList<>();
        for (Hok hok : hokken) {
            if (StringUtils.isNotBlank(hok.getPrijs())) {
                results.add(new LabelReportDecorator(hok));
            }
        }
        return results;
    }

    public static List<KampioenReportDecorator> getKampioenenForActiveEvent(Session session) {
        HokDao hokDao = new HokDao(session);
        Event event = getActiveEvent(session);
        List<Hok> hokken = hokDao.findByEventId(event.getId());
        Map<HoofdSoort,Map<Deelnemer,List<Hok>>> mapping = new HashMap<>();
        for (HoofdSoort hoofdSoort : HoofdSoort.values()) {
            mapping.put(hoofdSoort, new HashMap<>());
        }
        // divide hokken into hokken per deelnemer per hoofsoort
        for (Hok hok : hokken) {
            HoofdSoort hoofdSoort = HoofdSoort.getFromRas(hok.getInschrijvingLijn().getRas());
            if (hoofdSoort != null) {
                Map<Deelnemer, List<Hok>> deelnemerListMap = mapping.get(hoofdSoort);
                Deelnemer deelnemer = hok.getInschrijvingLijn().getInschrijving().getDeelnemer();
                deelnemerListMap.computeIfAbsent(deelnemer, k -> new ArrayList<>());
                List<Hok> deelnemerHokken = deelnemerListMap.get(deelnemer);
                deelnemerHokken.add(hok);
            } else {
                System.out.println("Geen hoofdsoort voor " + hok.getInschrijvingLijn().getRas().getSoort());
            }
        }

        List<KampioenReportDecorator> results = new ArrayList<>();
        for (HoofdSoort hoofdSoort : mapping.keySet()) {
            Map<Deelnemer, List<Hok>> deelnemerListMap = mapping.get(hoofdSoort);
            for (Deelnemer deelnemer : deelnemerListMap.keySet()) {
                List<Hok> deelnemerHokken = deelnemerListMap.get(deelnemer);
                boolean lidAvivaria = false;
                if (deelnemerHokken.size() >= 5) {
                    double totaal = 0.0;
                    int totaalJong = 0;
                    int totaalMan = 0;
                    // Sort hokken per deelnemer by predicaat (desc)
                    deelnemerHokken.sort((o1, o2) -> {
                        Double predicaat1 = getPredicaat(o1.getPredicaat());
                        Double predicaat2 = getPredicaat(o2.getPredicaat());
                        return predicaat2.compareTo(predicaat1);
                    });
                    // Use the first 5 hokken per deelnemer
                    for (int i = 0; i < 5; i++) {
                        Hok hok = deelnemerHokken.get(i);
                        lidAvivaria = hok.getInschrijvingLijn().getInschrijving().getLidAvivaria();
                        double predicaat = getPredicaat(hok.getPredicaat());
                        totaal += predicaat;
                        if (hok.getAantal().isMale()) totaalMan++;
                        if (StringUtils.isNotBlank(hok.getInschrijvingLijn().getLeeftijd())
                                && "jong".equalsIgnoreCase(hok.getInschrijvingLijn().getLeeftijd().trim())) {
                            totaalJong++;
                        }
                    }
                    results.add(new KampioenReportDecorator(hoofdSoort, deelnemer, totaal, totaalJong, totaalMan, lidAvivaria));
                }
            }
        }
        // sort
        Collections.sort(results);
        // set order
        HoofdSoort prevSoort = HoofdSoort.Cavia;
        double prevTotaal = 0.0;
        int prevTotaalJong = 0;
        int prevTotaalMan = 0;
        int order = 0;
        for (KampioenReportDecorator result : results) {
            if (prevSoort != result.getHoofdSoort()) {
                prevSoort = result.getHoofdSoort();
                prevTotaal = 0.0;
                prevTotaalJong = 0;
                prevTotaalMan = 0;
                order = 0;
            }
            if (prevTotaal != result.getTotaalValue() || prevTotaalJong != result.getTotaalJong() || prevTotaalMan != result.getTotaalMan()) {
                order++;
            }
            result.setOrder(order);
            prevTotaal = result.getTotaalValue();
            prevTotaalJong = result.getTotaalJong();
            prevTotaalMan = result.getTotaalMan();
        }

        return results;
    }

    private static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    private static Double getPredicaat(String predString) {
        Double predicaat = 0.0;
        predString = predString == null ? "" : predString.replace(",",".");
        predString = predString.replace("(","").replace(")","");
        if (predString.contains("/")) {
            int index = predString.indexOf("/");
            predString = predString.substring(0,index);
        }
        if (NumberUtils.isDecimal(predString.trim())) {
            predicaat = Double.valueOf(predString.trim());
        }
        return predicaat;
    }
}


