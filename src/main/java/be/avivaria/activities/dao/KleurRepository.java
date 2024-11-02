package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Kleur;
import be.avivaria.activities.model.Ras;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Transactional
public interface KleurRepository extends JpaRepository<Kleur, Long> {

    @Query("from Kleur where id = :id")
    Optional<Kleur> findById(@Param("id") Long id);
    List<Kleur> findAllByOrderByNaam();
    @Query("select distinct l.kleur from InschrijvingLijn l where l.inschrijving.event = :event order by l.kleur.naam")
    List<Kleur> findAllByEventOrderByNaam(@Param("event") Event event);
    @Query("select distinct l.kleur from InschrijvingLijn l where l.inschrijving.event = :event and l.ras = :ras order by l.kleur.naam")
    List<Kleur> findAllByRasAndEventOrderByNaam(@Param("event") Event event, @Param("ras") Ras ras);
}
