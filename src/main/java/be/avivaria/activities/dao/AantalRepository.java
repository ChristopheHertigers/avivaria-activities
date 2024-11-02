package be.avivaria.activities.dao;

import be.avivaria.activities.model.Aantal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AantalRepository extends JpaRepository<Aantal, Long> {

    List<Aantal> findAllByOrderByNaam();
}
