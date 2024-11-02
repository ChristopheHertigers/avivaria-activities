package be.avivaria.activities.dao;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeelnemerRepository extends JpaRepository<Deelnemer, Long> {

    List<Deelnemer> findAllByOrderByNaam();

    @Query("select i.deelnemer from InschrijvingHeader i where i.event = :event order by i.deelnemer.naam")
    List<Deelnemer> findAllByEventOrderByNaam(@Param("event") Event event);
}
