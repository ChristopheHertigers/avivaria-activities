package be.avivaria.activities.dao;

import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.HokType;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: christophe
 * Date: 30/10/13
 */
public class HokDao extends Dao<Hok> {
    public HokDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Hok> getManagedEntity() {
        return Hok.class;
    }

    @SuppressWarnings("unchecked")
    public List<Hok> findByEventId(long eventId) {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("inschrijvingLijn","l")
                .createAlias("inschrijvingLijn.inschrijving","i")
                .add(Restrictions.eq("i.event.id", eventId))
                .addOrder(Order.asc("hoknummer"))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Hok> findByEventIdOrderedByInschrijving(long eventId) {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("inschrijvingLijn","l")
                .createAlias("inschrijvingLijn.inschrijving","i")
                .add(Restrictions.eq("i.event.id", eventId))
                .addOrder(Order.asc("i.volgnummer"))
                .addOrder(Order.asc("hoknummer"))
                .list();
    }

    public void deleteByEventId(long eventId) {
        getPersistenceContext()
                .createQuery("delete Hok where inschrijvingLijn.id in (select i.id from InschrijvingLijn i where i.inschrijving.event.id = :eventId)")
                .setLong("eventId", eventId)
                .executeUpdate();
    }

    public long countForEvent(long eventId) {
        return ((Long) getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("inschrijvingLijn", "l")
                .createAlias("inschrijvingLijn.inschrijving", "i")
                .add(Restrictions.eq("i.event.id", eventId))
                .setProjection(Projections.count("id"))
                .uniqueResult());
    }

    public Map<HokType, Long> countPerHokTypeByEventId(long eventId) {
        List results = getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("inschrijvingLijn", "l")
                .createAlias("inschrijvingLijn.inschrijving", "i")
                .add(Restrictions.eq("i.event.id", eventId))
                .setProjection(Projections.projectionList().add(Projections.groupProperty("type")).add(Projections.count("id")))
                .list();
        Map<HokType, Long> counts = new HashMap<>();
        for (Object result : results) {
            HokType type = (HokType)((Object[])result)[0];
            Long count = (Long)((Object[])result)[1];
            counts.put(type,count);
        }
        return counts;
    }
}
