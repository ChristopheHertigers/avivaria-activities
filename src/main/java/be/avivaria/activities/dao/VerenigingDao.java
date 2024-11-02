package be.avivaria.activities.dao;

import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Vereniging;
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
public class VerenigingDao extends Dao<Vereniging> {
    public VerenigingDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Vereniging> getManagedEntity() {
        return Vereniging.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Vereniging> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.asc("naam"))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<Vereniging> findByEventId(long eventId) {
        return getPersistenceContext()
                .createQuery("SELECT distinct d.vereniging FROM InschrijvingHeader i JOIN i.deelnemer d WHERE i.event.id = :eventId ORDER BY d.vereniging.naam")
                .setLong("eventId",eventId)
                .list();
    }

}
