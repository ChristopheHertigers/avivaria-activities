package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.InschrijvingHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InschrijvingRepository extends JpaRepository<InschrijvingHeader, Long> {

    long countByEvent(Event event);

    List<InschrijvingHeader> findAllByEventOrderByDeelnemer_Naam(Event event);

    @Query("select max(volgnummer) from InschrijvingHeader where event = :event")
    Long maxVolgnummerByEvent(@Param("event") Event event);

    default Long nextVolgnummerByEvent(Event event) {
        Long max = maxVolgnummerByEvent(event);
        if (max == null || max == 0L) return 1L;
        return max + 1;
    }

}
