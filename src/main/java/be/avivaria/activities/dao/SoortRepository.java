package be.avivaria.activities.dao;

import be.avivaria.activities.model.Soort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoortRepository extends JpaRepository<Soort, Long> {

    List<Soort> findAllByOrderByNaam();
}
