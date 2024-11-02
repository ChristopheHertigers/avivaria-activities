package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.HokType;
import be.avivaria.activities.model.InschrijvingHeader;
import be.avivaria.activities.model.InschrijvingLijn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public interface InschrijvingLijnRepository extends JpaRepository<InschrijvingLijn, Long> {

        /*
    select i.VOLGNUMMER, d.NAAM, count(a.AANTAL2) as aantal
from INSCHRIJVING_HEADER i
join DEELNEMER d on d.ID = i.DEELNEMER_ID
join INSCHRIJVING_LIJN l on l.INSCHRIJVING_HEADER_ID = i.ID
join AANTAL a on a.ID = l.AANTAL_ID
where i.EVENT_ID = 40 and i.LID_AVIVARIA = 'True'
group by i.VOLGNUMMER, d.NAAM
order by d.naam
;
     */

    @Query("select l.inschrijving.deelnemer.naam, count(l.aantal.aantal2) from InschrijvingLijn l where l.inschrijving.event = :event and l.inschrijving.lidAvivaria = :lidAvivaria group by l.inschrijving.deelnemer.naam order by l.inschrijving.deelnemer.naam")
    List<Object[]> _countPerDeelnemerByEvent(@Param("event") Event event, @Param("lidAvivaria") boolean lid);

    default Map<String,Long> countPerDeelnemerByEvent(Event event, boolean lid) {
        return _countPerDeelnemerByEvent(event, lid).stream().collect(
                Collectors.toMap(
                        o -> (String) o[0],
                        o -> (Long) o[1],
                        Long::sum,
                        LinkedHashMap::new
                )
        );
    }

    List<InschrijvingLijn> findAllByInschrijvingOrderById(InschrijvingHeader inschrijving);
    List<InschrijvingLijn> findAllByInschrijving_EventOrderById(Event event);
}
