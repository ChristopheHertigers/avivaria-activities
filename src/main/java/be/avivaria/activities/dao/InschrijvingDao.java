package be.avivaria.activities.dao;

import be.avivaria.activities.model.InschrijvingHeader;
import be.indigosolutions.framework.dao.Dao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * User: christophe
 * Date: 11/10/13
 * Time: 21:35
 */
public class InschrijvingDao extends Dao<InschrijvingHeader> {
    private static final Logger LOGGER = LogManager.getLogger(InschrijvingDao.class);

    public InschrijvingDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<InschrijvingHeader> getManagedEntity() {
        return InschrijvingHeader.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<InschrijvingHeader> findAll() {
        LOGGER.debug("InschrijvingDao.findAll()");
        return getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("deelnemer", "d")
                .addOrder(Order.asc("d.naam"))
                .list();
    }

    @SuppressWarnings("unchecked")
    public List<InschrijvingHeader> findByEventId(long eventId) {
        LOGGER.debug("InschrijvingDao.findByEventId("+eventId+")");
        return getPersistenceContext().createCriteria(getManagedEntity())
                .createAlias("deelnemer", "d")
                .add(Restrictions.eq("event.id", eventId))
                .addOrder(Order.asc("d.naam"))
                .list();
    }

    public long countForEvent(long eventId) {
        LOGGER.debug("InschrijvingDao.countForEvent("+eventId+")");
        return ((Long) getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("event.id",eventId))
                .setProjection(Projections.count("id"))
                .uniqueResult());
    }

    public long getNextVolgnummer(long eventId) {
        LOGGER.debug("InschrijvingDao.getNextVolgnummer("+eventId+")");
        Long volgnummer = (Long) getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("event.id", eventId))
                .setProjection(Projections.max("volgnummer"))
                .uniqueResult();
        if (volgnummer == null) return 1L;
        return volgnummer + 1;
    }

}
