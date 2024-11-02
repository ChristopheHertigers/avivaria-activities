package be.avivaria.activities.dao;

import be.avivaria.activities.model.Kleur;
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
public class KleurDao extends Dao<Kleur> {
    public KleurDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Kleur> getManagedEntity() {
        return Kleur.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Kleur> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.asc("naam"))
                .list();
    }
}
