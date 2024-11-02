package be.avivaria.activities.model.usertype;

import be.avivaria.activities.model.HokType;
import be.indigosolutions.framework.dao.EnumIntType;
import org.hibernate.usertype.UserType;

/**
 * User: christophe
 * Date: 30/10/13
 */
public class HokTypeUserType extends EnumIntType<HokType> implements UserType {
    @Override
    public Class returnedClass() {
        return HokType.class;
    }

    @Override
    protected HokType fromDB(int code) {
        return HokType.fromType(code);
    }

    @Override
    protected int toDB(HokType value) {
        return value.getType();
    }
}
