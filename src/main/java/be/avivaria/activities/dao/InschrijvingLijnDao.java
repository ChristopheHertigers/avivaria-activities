package be.avivaria.activities.dao;

import be.avivaria.activities.model.InschrijvingLijn;
import be.indigosolutions.framework.dao.Dao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * User: christophe
 * Date: 11/10/13
 * Time: 21:35
 */
public class InschrijvingLijnDao extends Dao<InschrijvingLijn> {
    private static final Logger LOGGER = LogManager.getLogger(InschrijvingLijnDao.class);

    public InschrijvingLijnDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<InschrijvingLijn> getManagedEntity() {
        return InschrijvingLijn.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InschrijvingLijn> findAll() {
        LOGGER.debug("InschrijvingLijnDao.findAll()");
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.asc("id"))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<InschrijvingLijn> findByInschrijvingId(long inschrijvingId) {
        LOGGER.debug("InschrijvingLijnDao.findByInschrijvingId("+inschrijvingId+")");
        return getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("inschrijving.id", inschrijvingId))
                .addOrder(Order.asc("id"))
                .list();
    }

    public List<InschrijvingLijn> findByEventId(long eventId) {
        LOGGER.debug("InschrijvingLijnDao.findByEventId("+eventId+")");
        return getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("inschrijving", "i")
                .add(Restrictions.eq("i.event.id", eventId))
                .addOrder(Order.asc("i.volgnummer"))
                .addOrder(Order.asc("id"))
                .list();
    }

}
