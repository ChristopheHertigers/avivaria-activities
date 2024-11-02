package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Keurmeester;
import be.avivaria.activities.model.Ras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeurmeesterRepository extends JpaRepository<Keurmeester, Long> {

    @Query("from Keurmeester where event = :event order by naam")
    List<Keurmeester> findAllByEventOrderByNaam(@Param("event") Event event);

}
