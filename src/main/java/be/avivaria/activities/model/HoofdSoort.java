package be.avivaria.activities.model;

import java.util.Arrays;
import java.util.List;

/**
 * User: christophe
 * Date: 05/11/13
 */
@SuppressWarnings("SpellCheckingInspection")
public enum HoofdSoort {
    Cavia("Cavia's", new Long[]{10L}),
    Konijn("Konijnen", new Long[]{20L, 21L, 22L, 23L}),
    Duif("Duiven", new Long[]{30L, 31L, 32L, 33L, 34L, 35L, 36L, 37L, 38L}),
    Kriel("Krielen", new Long[]{50L}),
    Hoender("Hoenders", new Long[]{60L}),
    GedomesticeerdeParkEnWatervogel("Gedomesticeerde Park- en Watervogels", new Long[]{70L, 71L, 72L, 73L, 74L}),
    WildeParkEnWatervogel("Wilde Park- en Watervogels", new Long[]{78L, 80L, 81L, 82L, 83L, 84L, 85L, 86L, 87L, 100L, 101L, 102L, 103L, 104L, 105L, 106L, 107L, 108L, 121L, 122L});

    private String label;
    private List<Long> soortIds;

    private HoofdSoort(String label, Long[] soortIds) {
        this.label = label;
        this.soortIds = Arrays.asList(soortIds);
    }

    public String getLabel() {
        return label;
    }

    public List<Long> getSoortIds() {
        return soortIds;
    }

    public static HoofdSoort getFromRas(Ras ras) {
        if (ras == null || ras.getSoort() == null) return null;
        for (HoofdSoort hoofdSoort : values()) {
            if (hoofdSoort.soortIds.contains(ras.getSoort().getId())) {
                return hoofdSoort;
            }
        }
        return null;
    }
}

