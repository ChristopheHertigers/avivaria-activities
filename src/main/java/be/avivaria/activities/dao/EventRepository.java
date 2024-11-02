package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("from Event e where e.selected = true")
    Event findBySelectedTrue();

    List<Event> findAllByOrderByIdDesc();

    @Transactional
    @Modifying
    @Query("update Event e set e.selected = false")
    void deselectAll();

    @Transactional
    @Modifying
    @Query("update Event e set e.selected = true where e.id = :id")
    void select(@Param("id") Long id);
}
