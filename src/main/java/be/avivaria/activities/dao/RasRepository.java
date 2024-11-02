package be.avivaria.activities.dao;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Ras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RasRepository extends JpaRepository<Ras, Long> {
    List<Ras> findAllByOrderByNaam();

    @Query("select distinct ras from InschrijvingLijn where inschrijving.event = :event order by ras.naam")
    List<Ras> findAllByEventOrderByNaam(@Param("event") Event event);

    @Query("select distinct inschrijving.deelnemer, ras from InschrijvingLijn where inschrijving.event = :event order by inschrijving.deelnemer.naam, ras.naam")
    List<Object[]> _findRasListPerDeelnemerByEvent(@Param("event") Event event);

    default Map<Deelnemer, List<Ras>> findRasListPerDeelnemerByEvent(Event event) {
        Map<Deelnemer, List<Ras>> mapping = new HashMap<Deelnemer, List<Ras>>();
        List<Object[]> results = _findRasListPerDeelnemerByEvent(event);
        for (Object[] result : results) {
            Deelnemer deelnemer = (Deelnemer)result[0];
            Ras ras = (Ras)result[1];
            List<Ras> rasList = mapping.computeIfAbsent(deelnemer, d -> new ArrayList<>());
            rasList.add(ras);
        }
        return mapping;
    }

}
