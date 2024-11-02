package be.avivaria.activities.dao;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.EventType;
import be.indigosolutions.framework.dao.Dao;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:52
 */
public class EventDao extends Dao<Event> {
    public EventDao(Session persistenceContext) {
        super(persistenceContext);
    }

    @Override
    protected Class<Event> getManagedEntity() {
        return Event.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Event> findAll() {
        return getPersistenceContext().createCriteria(getManagedEntity())
                .addOrder(Order.desc("id"))
                .list();
    }

    public Event findSelected() {
        return (Event) getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("selected", true))
                .uniqueResult();
    }

    public void selectEvent(Event event) {
        Event selected = findSelected();
        if (selected != null) {
            selected.setSelected(false);
            update(selected);
        }
        event.setSelected(true);
        update(event);
    }

    @SuppressWarnings("unchecked")
    public Event findPreviousOfSelectedType() {
        Event selected = findSelected();
        List<Event> eventsOfType = getPersistenceContext().createCriteria(getManagedEntity())
                .add(Restrictions.eq("type",selected.getType()))
                .add(Restrictions.lt("id", selected.getId()))
                .addOrder(Order.desc("id"))
                .list();
        if (eventsOfType != null && eventsOfType.size() > 0) {
            return eventsOfType.get(0);
        }
        return null;
    }
}
