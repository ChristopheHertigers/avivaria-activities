package be.avivaria.activities.model.usertype;

import be.avivaria.activities.model.EventType;
import be.indigosolutions.framework.dao.EnumStringType;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 30/10/13
 * Time: 18:15
 * To change this template use File | Settings | File Templates.
 */
public class EventTypeUserType extends EnumStringType<EventType> {
    @Override
    public Class<EventType> returnedClass() {
        return EventType.class;
    }

    @Override
    protected EventType fromDB(String code) {
        return EventType.fromCode(code);
    }

    @Override
    protected String toDB(EventType value) {
        if (value == null) return null;
        return value.getCode();
    }
}
