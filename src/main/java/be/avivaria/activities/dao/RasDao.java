package be.avivaria.activities.dao;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Ras;
import be.avivaria.activities.model.Soort;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:52
 */
public class RasDao extends Dao<Ras> {
    public RasDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Ras> getManagedEntity() {
        return Ras.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Ras> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.asc("naam"))
                .list();
    }

    public Map<Deelnemer, List<Ras>> findRasListPerDeelnemerByEventId(long eventId) {
        Map<Deelnemer, List<Ras>> mapping = new HashMap<Deelnemer, List<Ras>>();
        List results = getPersistenceContext()
                .createQuery("SELECT DISTINCT h.deelnemer, l.ras FROM InschrijvingLijn l JOIN l.inschrijving h WHERE h.event.id = :eventId ORDER BY h.deelnemer.naam, l.ras.naam")
                .setLong("eventId",eventId)
                .list();
        for (Object result : results) {
            Deelnemer deelnemer = (Deelnemer)((Object[])result)[0];
            Ras ras = (Ras)((Object[])result)[1];
            List<Ras> rasList = mapping.get(deelnemer);
            if (rasList == null) {
                rasList = new ArrayList<Ras>();
                mapping.put(deelnemer, rasList);
            }
            rasList.add(ras);
        }
        return mapping;
    }
}
