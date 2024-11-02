package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.HokType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface HokRepository extends JpaRepository<Hok, Long> {

    @Query("select h.type, count(h) from Hok h where h.inschrijvingLijn.inschrijving.event = :event group by h.type")
    List<Object[]> _countPerHokTypeByEvent(@Param("event") Event event);

    default Map<HokType,Long> countPerHokTypeByEvent(Event event) {
        return _countPerHokTypeByEvent(event).stream().collect(
                Collectors.toMap(
                        o -> (HokType) o[0],
                        o -> (Long) o[1]
                )
        );
    }

    @Query("select count(h) from Hok h where h.inschrijvingLijn.inschrijving.event = :event")
    long countByEvent(@Param("event") Event event);

    @Transactional
    @Modifying
    void deleteByInschrijvingLijn_Inschrijving_Event(Event event);

    @Query("from Hok where inschrijvingLijn.inschrijving.event = :event order by hoknummer")
    List<Hok> findAllByEventOrderByHoknummer(@Param("event") Event event);

    @Query("from Hok where inschrijvingLijn.inschrijving.event = :event order by inschrijvingLijn.inschrijving.volgnummer, hoknummer")
    List<Hok> findAllByEventOrderByInschrijving(@Param("event") Event event);
}
