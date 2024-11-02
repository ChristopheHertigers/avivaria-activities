package be.avivaria.activities.model;

import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 30/10/13
 * Time: 18:14
 * To change this template use File | Settings | File Templates.
 */
public enum EventType {
    Tentoonstelling("TT") {
        @Override
        public Comparator<Hok> getHokComparator() {
            return new Comparator<Hok>() {
                @Override
                public int compare(Hok o1, Hok o2) {
                    // sort by
                    // 1. Soort naam (oplopend per id, geeft volgorde aan)
                    int i = Long.valueOf(o1.getInschrijvingLijn().getRas().getSoort().getId()).compareTo(o2.getInschrijvingLijn().getRas().getSoort().getId());
                    if (i != 0) return i;
                    // 2. Hok type (rassen met een kleiner hok eerst)
                    i = -1 * Integer.valueOf(o1.getInschrijvingLijn().getRas().getSmallestHokType().getType()).compareTo(o2.getInschrijvingLijn().getRas().getSmallestHokType().getType());
                    if (i != 0) return i;
                    // 3. Belgische rassen (eerst, daarna niet belgisch)
                    i = -1 * Boolean.valueOf(o1.getInschrijvingLijn().getRas().isBelgisch()).compareTo(o2.getInschrijvingLijn().getRas().isBelgisch());
                    if (i != 0) return i;
                    // 4. Ras volgorde (indien afwijkend van default 1000)
                    i = o1.getInschrijvingLijn().getRas().getVolgorde().compareTo(o2.getInschrijvingLijn().getRas().getVolgorde());
                    if (i != 0) return i;
                    // 5. Ras naam (alphabetisch)
                    i = o1.getInschrijvingLijn().getRas().getNaam().compareTo(o2.getInschrijvingLijn().getRas().getNaam());
                    if (i != 0) return i;
                    // 6. Kleur (alphabetisch)
                    i = o1.getInschrijvingLijn().getKleur().getNaam().compareTo(o2.getInschrijvingLijn().getKleur().getNaam());
                    if (i != 0) return i;
                    // 7. Geslacht (man voor vrouw)
                    i = Long.valueOf(o1.getAantal().getId()).compareTo(o2.getAantal().getId());
                    if (i != 0) return i;
                    // 8. Leeftijd (jong voor oud)
                    i = o1.getInschrijvingLijn().getLeeftijd().compareTo(o2.getInschrijvingLijn().getLeeftijd());
                    if (i != 0) return i;
                    // 9. Inschrijving volgnummer
                    i = o1.getInschrijvingLijn().getInschrijving().getVolgnummer().compareTo(o2.getInschrijvingLijn().getInschrijving().getVolgnummer());
                    if (i != 0) return i;
                    // 10. Inschrijving lijn id
                    i = Long.valueOf(o1.getInschrijvingLijn().getId()).compareTo(o2.getInschrijvingLijn().getId());
                    return i;
                }
            };
        }
    },
    Verkoopdag("VD") {
        @Override
        public Comparator<Hok> getHokComparator() {
            return new Comparator<Hok>() {
                @Override
                public int compare(Hok o1, Hok o2) {
                    // sort by
                    // 1. Soort naam (oplopend per id, geeft volgorde aan)
                    int i = Long.valueOf(o1.getInschrijvingLijn().getRas().getSoort().getId()).compareTo(o2.getInschrijvingLijn().getRas().getSoort().getId());
                    if (i != 0) return i;
                    // 2. Hok type (rassen met een kleiner hok eerst)
                    i = -1 * Integer.valueOf(o1.getInschrijvingLijn().getRas().getSmallestHokType().getType()).compareTo(o2.getInschrijvingLijn().getRas().getSmallestHokType().getType());
                    if (i != 0) return i;
                    // 3. Belgische rassen (eerst, daarna niet belgisch)
                    i = -1 * Boolean.valueOf(o1.getInschrijvingLijn().getRas().isBelgisch()).compareTo(o2.getInschrijvingLijn().getRas().isBelgisch());
                    if (i != 0) return i;
                    // 4. Ras volgorde (indien afwijkend van default 1000)
                    i = o1.getInschrijvingLijn().getRas().getVolgorde().compareTo(o2.getInschrijvingLijn().getRas().getVolgorde());
                    if (i != 0) return i;
                    // 5. Ras naam (alphabetisch)
                    i = o1.getInschrijvingLijn().getRas().getNaam().compareTo(o2.getInschrijvingLijn().getRas().getNaam());
                    if (i != 0) return i;
                    // 6. Kleur (alphabetisch)
                    i = o1.getInschrijvingLijn().getKleur().getNaam().compareTo(o2.getInschrijvingLijn().getKleur().getNaam());
                    if (i != 0) return i;
                    // 7. Geslacht (man voor vrouw)
                    i = Long.valueOf(o1.getAantal().getId()).compareTo(o2.getAantal().getId());
                    if (i != 0) return i;
                    // 8. Leeftijd (jong voor oud)
                    i = o1.getInschrijvingLijn().getLeeftijd().compareTo(o2.getInschrijvingLijn().getLeeftijd());
                    if (i != 0) return i;
                    // 9. Inschrijving volgnummer
                    i = o1.getInschrijvingLijn().getInschrijving().getVolgnummer().compareTo(o2.getInschrijvingLijn().getInschrijving().getVolgnummer());
                    if (i != 0) return i;
                    // 10. Inschrijving lijn id
                    i = Long.valueOf(o1.getInschrijvingLijn().getId()).compareTo(o2.getInschrijvingLijn().getId());
                    return i;
                }
            };
        }
    };

    private String code;

    private EventType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        // do nothing
    }

    public static EventType fromCode(String code) {
        for (EventType type : values()) {
            if (type.getCode().equals(code)) return type;
        }
        return null;
    }

    public abstract Comparator<Hok> getHokComparator();
}
