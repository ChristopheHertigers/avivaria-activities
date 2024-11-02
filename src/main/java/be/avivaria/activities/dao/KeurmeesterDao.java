package be.avivaria.activities.dao;

import be.avivaria.activities.model.Keurmeester;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * User: christophe
 * Date: 11/11/13
 */
public class KeurmeesterDao extends Dao<Keurmeester> {
    public KeurmeesterDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Keurmeester> getManagedEntity() {
        return Keurmeester.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Keurmeester> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity()).addOrder(Order.asc("naam")).list();
    }

    @SuppressWarnings("unchecked")
    public List<Keurmeester> findByEventId(long eventId) {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("event.id", eventId))
                .addOrder(Order.asc("naam"))
                .list();
    }
}
