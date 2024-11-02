package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Vereniging;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VerenigingRepository extends JpaRepository<Vereniging, Long> {

    List<Vereniging> findAllByOrderByNaam();

    @Query("SELECT distinct deelnemer.vereniging FROM InschrijvingHeader WHERE event = :event ORDER BY deelnemer.vereniging.naam")
    List<Vereniging> findAllByEventOrderedByNaam(@Param("event") Event event);
}
