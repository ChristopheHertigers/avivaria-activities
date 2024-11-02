package be.avivaria.activities.dao;

import be.avivaria.activities.model.Aantal;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:52
 */
public class AantalDao extends Dao<Aantal> {
    public AantalDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Aantal> getManagedEntity() {
        return Aantal.class;
    }
}
