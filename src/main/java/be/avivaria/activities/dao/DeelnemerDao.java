package be.avivaria.activities.dao;

import be.avivaria.activities.model.Deelnemer;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;

import java.util.List;

/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:52
 */
public class DeelnemerDao extends Dao<Deelnemer> {
    public DeelnemerDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Deelnemer> getManagedEntity() {
        return Deelnemer.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Deelnemer> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.asc("naam"))
                .list();
    }

    public List<Deelnemer> findByEventId(long eventId) {
        return getPersistenceContext()
                .createQuery("SELECT i.deelnemer FROM InschrijvingHeader i WHERE i.event.id = :eventId ORDER BY i.deelnemer.naam")
                .setLong("eventId",eventId)
                .list();
    }
}
