package be.avivaria.activities.reports;

import be.avivaria.activities.dao.*;
import be.avivaria.activities.model.*;
import be.indigosolutions.framework.util.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * User: christophe
 * Date: 01/11/13
 */
@Component
public class ReportDataFactory {

    private final EventRepository eventRepository;
    private final DeelnemerRepository deelnemerRepository;
    private final RasRepository rasRepository;
    private final HokRepository hokRepository;
    private final VerenigingRepository verenigingRepository;
    private final InschrijvingLijnRepository inschrijvingLijnRepository;
    private final KeurmeesterRepository keurmeesterRepository;

    @Autowired
    public ReportDataFactory(
            EventRepository eventRepository,
            DeelnemerRepository deelnemerRepository,
            RasRepository rasRepository,
            HokRepository hokRepository,
            VerenigingRepository verenigingRepository,
            InschrijvingLijnRepository inschrijvingLijnRepository,
            KeurmeesterRepository keurmeesterRepository
    ) {
        this.eventRepository = eventRepository;
        this.deelnemerRepository = deelnemerRepository;
        this.rasRepository = rasRepository;
        this.hokRepository = hokRepository;
        this.verenigingRepository = verenigingRepository;
        this.inschrijvingLijnRepository = inschrijvingLijnRepository;
        this.keurmeesterRepository = keurmeesterRepository;
    }

    public Event getActiveEvent() {
        return eventRepository.findBySelectedTrue();
    }

    private List<DeelnemerReportDecorator> getDeelnemerInfo(Predicate<Deelnemer> filter) {
        Event event = getActiveEvent();
        Map<Deelnemer, List<Ras>> rassen = rasRepository.findRasListPerDeelnemerByEvent(event);
        return deelnemerRepository.findAllByEventOrderByNaam(event)
                .stream()
                .filter(filter)
                .map(d -> new DeelnemerReportDecorator(d, rassen.get(d)))
                .collect(Collectors.toList());
    }

    public List<DeelnemerReportDecorator> getDeelnemersForActiveEvent() {
        return getDeelnemerInfo(deelnemer -> deelnemer.getJeugddeelnemer() == null || deelnemer.getJeugddeelnemer() < (getCurrentYear()-18));
    }

    public List<DeelnemerReportDecorator> getJeugdDeelnemersForActiveEvent() {
        return getDeelnemerInfo(deelnemer -> deelnemer.getJeugddeelnemer() != null && deelnemer.getJeugddeelnemer() >= (getCurrentYear()-18));
    }

    public List<InschrijvingReportDecorator> getInschrijvingenForActiveEvent() {
        Event event = getActiveEvent();
        List<Hok> hokken = hokRepository.findAllByEventOrderByInschrijving(event);
        return new ArrayList<>(hokken)
                .stream()
                .map(h -> {
                    long aantalDieren = countDierenForInschrijving(hokken, h.getInschrijvingLijn().getInschrijving());
                    long aantalDierenTeKoop = countDierenTeKoopForInschrijving(hokken, h.getInschrijvingLijn().getInschrijving());
                    return new InschrijvingReportDecorator(h, aantalDieren, aantalDierenTeKoop);
                })
                .collect(Collectors.toList());
    }

    private long countDierenForInschrijving(List<Hok> hokken, InschrijvingHeader inschrijving) {
        return hokken.stream()
                .filter(h -> h.getInschrijvingLijn().getInschrijving().getId().equals(inschrijving.getId()))
                .mapToLong(h -> h.getAantal().getAantal2().longValue())
                .sum();
    }

    private long countDierenTeKoopForInschrijving(List<Hok> hokken, InschrijvingHeader inschrijving) {
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

    public List<PalmaresReportDecorator> getPalmaresForActiveEvent() {
        Event event = getActiveEvent();
        List<Keurmeester> keurmeesters = keurmeesterRepository.findAllByEventOrderByNaam(event);
        Map<String, List<KeurmeesterMapping>> keurmeestersPerRas = new HashMap<>();
        keurmeesters.forEach(k -> keurmeestersPerRas
                .computeIfAbsent(k.getRas().getNaam(), (key) -> new ArrayList<>())
                .add(new KeurmeesterMapping(k.getNaam(), k.getRas().getNaam(), k.getKleur() == null || k.getKleur().getId() == 0L ? null : k.getKleur().getNaam())));
        List<PalmaresReportDecorator> palmares = hokRepository.findAllByEventOrderByHoknummer(event)
                .stream()
                .map(hok -> {
                    List<KeurmeesterMapping> perRas = keurmeestersPerRas.getOrDefault(hok.getInschrijvingLijn().getRas().getNaam(), new ArrayList<>());
                    String ras = "";
                    String kleur = "";
                    if (perRas.size() == 1) ras = perRas.get(0).naam;
                    if (perRas.size() > 1) {
                        Optional<KeurmeesterMapping> geenKleur = perRas.stream().filter(k -> k.kleurNaam == null).findAny();
                        Optional<KeurmeesterMapping> metKleur = perRas.stream().filter(k -> hok.getInschrijvingLijn().getKleur().getNaam().equals(k.kleurNaam)).findAny();
                        if (metKleur.isPresent()) kleur = metKleur.get().naam;
                        else if (geenKleur.isPresent()) kleur = geenKleur.get().naam;
                    }
                    return new PalmaresReportDecorator(hok, ras, kleur, "");
                })
                .toList();
        addBreaks(palmares);
        return palmares;
    }

    private static final int PAGE_HEIGHT = 670;
    private static final int SOORT_HEADER = 28;
    private static final int RAS_HEADER = 24;
    private static final int KLEUR_HEADER = 22;
    private static final int LEEFTIJD_HEADER = 15;
    private static final int DETAIL = 15;
    private void addBreaks(List<PalmaresReportDecorator> palmares) {
        List<RecordHeight> recordHeights = computeHeights(palmares);
        int i = 0;
        int currentHeight = SOORT_HEADER;
        for (RecordHeight soort : recordHeights) {
            // always add a break after soort
            currentHeight = SOORT_HEADER;
            if (soort.height <= PAGE_HEIGHT) {
                System.out.println(soort.group + " fits on page, no split [" + soort.height + "/" + PAGE_HEIGHT + "]");

                while (i < palmares.size() && soort.group.equals(palmares.get(i).getSoort())) {
                    i++;
                }

            } else {
                System.out.println(soort.group + " does not fit on page, need to split. HELP! [" + soort.height + "/" + PAGE_HEIGHT + "]");

                for (RecordHeight ras : soort.children) {
                    System.out.println("Current height: " + currentHeight);
                    if (ras.height <= (PAGE_HEIGHT - currentHeight)) {
                        System.out.println(ras.group + " fits on current page, phew [" + ras.height + "/" + (PAGE_HEIGHT - currentHeight) + "]");
                        while (i < palmares.size() && ras.group.equals(palmares.get(i).getRas())) {
                            i++;
                        }
                        currentHeight += ras.height;

                    } else if (ras.height <= (PAGE_HEIGHT - SOORT_HEADER)) {
                        System.out.println(ras.group + " fits on page, phew [" + ras.height + "/" + (PAGE_HEIGHT - SOORT_HEADER) + "]");
                        // does not fit on current page, but fits on full page
                        // add page break before
                        palmares.get(i-1).setAddBreak("true");
                        while (i < palmares.size() && ras.group.equals(palmares.get(i).getRas())) {
                            i++;
                        }
                        currentHeight = ras.height;

                    } else {
                        System.out.println(ras.group + " does not fit on page, need to split. NO NO NO! [" + ras.height + "/" + (PAGE_HEIGHT - SOORT_HEADER) + "]");

//                        Antwerpse baardkriel does not fit on page, need to split. NO NO NO! [658/642]
//                        checking blauw with height 52 / 91
//                        blauw fits on current page, man man [52/91]
//                        checking blauw gezoomd with height 82 / 39
//                        blauw gezoomd fits on page, man man [82/618]
//                        checking duizendkleur with height 112 / 536
//                        duizendkleur fits on current page, man man [112/536]
//                        checking kwartel with height 112 / 424
//                        kwartel fits on current page, man man [112/424]
//                        checking parelgrijskwartel with height 112 / 312
//                        parelgrijskwartel fits on current page, man man [112/312]
//                        checking splash with height 52 / 200
//                        splash fits on current page, man man [52/200]
//                        checking zwart with height 112 / 148
//                        zwart fits on current page, man man [112/148]

                        for (RecordHeight kleur : ras.children) {
                            System.out.println("checking " + kleur.group + " with height " + kleur.height + " / " + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - currentHeight));
                            if (kleur.height <= (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - currentHeight)) {
                                System.out.println(kleur.group + " fits on current page, man man [" + kleur.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - currentHeight) + "]");
                                while (i < palmares.size() && kleur.group.equals(palmares.get(i).getKleur())) {
                                    i++;
                                }
                                currentHeight += kleur.height;

                            } else if (kleur.height <= (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER)) {
                                System.out.println(kleur.group + " fits on page, man man [" + kleur.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER) + "]");
                                // does not fit on current page, but fits on full page
                                // add page break before
                                palmares.get(i-1).setAddBreak("true");
                                while (i < palmares.size() && kleur.group.equals(palmares.get(i).getKleur())) {
                                    i++;
                                }
                                currentHeight = kleur.height;

                            } else {
                                System.out.println(kleur.group + " does not fit on page, need to split. Fuck this shit! [" + kleur.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER) + "]");

                                for (RecordHeight leeftijd : kleur.children) {
                                    if (leeftijd.height <= (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER - currentHeight)) {
                                        System.out.println(leeftijd.group + " fits on current page, mahjong [" + leeftijd.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER) + "]");
                                        while (i < palmares.size() && leeftijd.group.equals(palmares.get(i).getGeslachtEnLeeftijd())) {
                                            i++;
                                        }
                                        currentHeight += leeftijd.height;

                                    } else if (leeftijd.height <= (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER)) {
                                        System.out.println(leeftijd.group + " fits on page, mahjong [" + leeftijd.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER) + "]");
                                        // does not fit on current page, but fits on full page
                                        // add page break before
                                        palmares.get(i-1).setAddBreak("true");
                                        while (i < palmares.size() && leeftijd.group.equals(palmares.get(i).getGeslachtEnLeeftijd())) {
                                            i++;
                                        }
                                        currentHeight = leeftijd.height;

                                    } else {
                                        System.out.println(leeftijd.group + " does not fit on page, need to split. Fuck this shit! [" + kleur.height + "/" + (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER) + "]");

                                        while (i < palmares.size() && leeftijd.group.equals(palmares.get(i).getGeslachtEnLeeftijd())) {
                                            if ((currentHeight + DETAIL) > (PAGE_HEIGHT - SOORT_HEADER - RAS_HEADER - KLEUR_HEADER)) {
                                                palmares.get(i).setAddBreak("true");
                                                currentHeight = 0;
                                            }
                                            i++;
                                        }


                                    }
                                }

                            }
                        }
                    }
                }

            }

            if (i < palmares.size()) {
                palmares.get(i-1).setAddBreak("true");
            }

        }
    }

    private List<RecordHeight> computeHeights(List<PalmaresReportDecorator> palmares) {
        List<RecordHeight> recordHeights = new ArrayList<>();
        RecordHeight currentSoort = null;
        RecordHeight currentRas = null;
        RecordHeight currentKleur = null;
        RecordHeight currentLeeftijd = null;

        for (PalmaresReportDecorator record : palmares) {
            if (currentSoort == null || !currentSoort.group.equals(record.getSoort()) ) {
                currentSoort = new RecordHeight(record.getSoort(), SOORT_HEADER + RAS_HEADER + KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentRas = new RecordHeight(record.getRas(), RAS_HEADER + KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentKleur = new RecordHeight(record.getKleur(), KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentLeeftijd = new RecordHeight(record.getGeslachtEnLeeftijd(), LEEFTIJD_HEADER + DETAIL);
                currentKleur.children.add(currentLeeftijd);
                currentRas.children.add(currentKleur);
                currentSoort.children.add(currentRas);
                recordHeights.add(currentSoort);
            } else if (!currentRas.group.equals(record.getRas())) {
                currentSoort.height += RAS_HEADER + KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL;
                currentRas = new RecordHeight(record.getRas(), RAS_HEADER + KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentKleur = new RecordHeight(record.getKleur(), KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentLeeftijd = new RecordHeight(record.getGeslachtEnLeeftijd(), LEEFTIJD_HEADER + DETAIL);
                currentKleur.children.add(currentLeeftijd);
                currentRas.children.add(currentKleur);
                currentSoort.children.add(currentRas);
            } else if (!currentKleur.group.equals(record.getKleur())) {
                currentSoort.height += KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL;
                currentRas.height += KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL;
                currentKleur = new RecordHeight(record.getKleur(), KLEUR_HEADER + LEEFTIJD_HEADER + DETAIL);
                currentLeeftijd = new RecordHeight(record.getGeslachtEnLeeftijd(), LEEFTIJD_HEADER + DETAIL);
                currentKleur.children.add(currentLeeftijd);
                currentRas.children.add(currentKleur);
            } else if (!currentLeeftijd.group.equals(record.getGeslachtEnLeeftijd())) {
                currentSoort.height += LEEFTIJD_HEADER + DETAIL;
                currentRas.height += LEEFTIJD_HEADER + DETAIL;
                currentKleur.height += LEEFTIJD_HEADER + DETAIL;
                currentLeeftijd = new RecordHeight(record.getGeslachtEnLeeftijd(), LEEFTIJD_HEADER + DETAIL);
                currentKleur.children.add(currentLeeftijd);
            } else {
                currentSoort.height += DETAIL;
                currentRas.height += DETAIL;
                currentKleur.height += DETAIL;
                currentLeeftijd.height += DETAIL;
            }
        }

        for (RecordHeight recordHeight : recordHeights) {
            System.out.println(recordHeight);
        }
        return recordHeights;
    }

    private static class RecordHeight {
        final String group;
        int height;
        final List<RecordHeight> children = new ArrayList<>();

        public RecordHeight(String group, int height) {
            this.group = group;
            this.height = height;
        }

        public String toString() {
            StringBuilder b = new StringBuilder().append(group).append(" :: ").append(height).append("\n");
            for (RecordHeight child : children) {
                b.append(child.toString());
            }
            return b.toString();
        }
    }

    private record KeurmeesterMapping(String naam, String rasNaam, String kleurNaam) {};

    private Keurmeester matchKeurmeester(List<Keurmeester> keurmeesters, Hok hok) {
        return null;
    }

    public List<VerenigingReportDecorator> getVerenigingenForActiveEvent() {
        Event event = getActiveEvent();
        List<Hok> hokken = hokRepository.findAllByEventOrderByHoknummer(event);
        return verenigingRepository.findAllByEventOrderedByNaam(event)
                .stream()
                .map(v -> {
                    long aantalDeelnemers = countDeelnemersForVereniging(hokken, v);
                    long aantalDieren = countDierenForVereniging(hokken, v);
                    return new VerenigingReportDecorator(v, aantalDeelnemers, aantalDieren);
                })
                .collect(Collectors.toList());
    }

    private long countDeelnemersForVereniging(List<Hok> hokken, Vereniging vereniging) {
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

    private long countDierenForVereniging(List<Hok> hokken, Vereniging vereniging) {
        long aantal = 0L;
        for (Hok hok : hokken) {
            Deelnemer deelnemer = hok.getInschrijvingLijn().getInschrijving().getDeelnemer();
            if (deelnemer.getVereniging() != null && deelnemer.getVereniging().getId().equals(vereniging.getId())) {
                aantal += hok.getAantal().getAantal2();
            }
        }
        return aantal;
    }

    public List<LabelReportDecorator> getHokLabelsForActiveEvent() {
        return hokRepository.findAllByEventOrderByHoknummer(getActiveEvent())
                .stream()
                .map(LabelReportDecorator::new)
                .collect(Collectors.toList());
    }

    public List<LabelReportDecorator> getPrijsLabelsForActiveEvent() {
        return hokRepository.findAllByEventOrderByHoknummer(getActiveEvent())
                .stream()
                .filter(h -> StringUtils.isNotBlank(h.getPrijs()))
                .map(LabelReportDecorator::new)
                .collect(Collectors.toList());
    }

    public List<AantalDierenReportDecorator> getAantalDieren(boolean lidAvivaria) {
        return inschrijvingLijnRepository.countPerDeelnemerByEvent(getActiveEvent(), lidAvivaria)
                .entrySet()
                .stream()
                .map(entry -> new AantalDierenReportDecorator(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<KampioenReportDecorator> getKampioenenForActiveEvent() {
        List<Hok> hokken = hokRepository.findAllByEventOrderByHoknummer(getActiveEvent());
        Map<HoofdSoort,Map<Deelnemer,List<Hok>>> mapping = new HashMap<>();
        for (HoofdSoort hoofdSoort : HoofdSoort.values()) {
            mapping.put(hoofdSoort, new HashMap<>());
        }
        // divide hokken into hokken per deelnemer per hoofsoort
        for (Hok hok : hokken) {
            List<HoofdSoort> hoofdSoorten = HoofdSoort.getFromRas(hok.getInschrijvingLijn().getRas());
            hoofdSoorten.forEach(hoofdSoort -> {
                Map<Deelnemer, List<Hok>> deelnemerListMap = mapping.get(hoofdSoort);
                Deelnemer deelnemer = hok.getInschrijvingLijn().getInschrijving().getDeelnemer();
                deelnemerListMap.computeIfAbsent(deelnemer, k -> new ArrayList<>()).add(hok);
            });
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
            if (!prevSoort.equals(result.getHoofdSoort())) {
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
        double predicaat = 0.0;
        predString = predString == null ? "" : predString.replace(",",".");
        predString = predString.replace("(","").replace(")","");
        if (predString.contains("/")) {
            int index = predString.indexOf("/");
            predString = predString.substring(0,index);
        }
        if (NumberUtils.isDecimal(predString.trim())) {
            predicaat = Double.parseDouble(predString.trim());
        }
        return predicaat;
    }
}


