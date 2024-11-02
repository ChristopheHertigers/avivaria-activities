package be.indigosolutions.framework.dao;

import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;

import java.io.Serializable;
import java.util.List;

/**
 * Base class for DAOs.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 8-aug-2008
 */
@SuppressWarnings("unchecked")
public abstract class Dao<T> {

    private Session persistenceContext;

    protected Dao(Session persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public List<T> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity()).list();
    }

    public T load(Serializable id) {
        return (T) getPersistenceContext().load(getManagedEntity(), id);
    }

    public T get(Serializable id) { return (T) getPersistenceContext().get(getManagedEntity(), id); }

    public void refresh(T instance) {
        getPersistenceContext().refresh(instance);
    }

    public void lock(T instance, LockMode mode) {
        getPersistenceContext().lock(instance, mode);
    }

    public void merge(T instance) { getPersistenceContext().merge(instance); }

    public void saveOrUpdate(T instance) {
        getPersistenceContext().saveOrUpdate(instance);
    }

    public void save(T instance) {
        getPersistenceContext().save(instance);
    }

    public void update(T instance) {
        getPersistenceContext().update(instance);
    }

    public void delete(T instance) {
        getPersistenceContext().delete(instance);
    }

    public void flush() {
        getPersistenceContext().flush();
    }

    protected abstract Class<T> getManagedEntity();

    protected Session getPersistenceContext() {
        return persistenceContext;
    }

    protected void setPersistenceContext(Session persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public long getNextId() {
        return ((Long) getPersistenceContext().createCriteria(getManagedEntity())
                .setProjection(Projections.max("id"))
                .uniqueResult()) + 1;
    }

}
