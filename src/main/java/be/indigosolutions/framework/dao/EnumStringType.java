package be.indigosolutions.framework.dao;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User: christophe
 * Date: 30/10/13
 */
public abstract class EnumStringType<T> implements UserType<T> {
    @Override
    public int getSqlType() {
        return Types.VARCHAR;
    }

    @Override
    public abstract Class<T> returnedClass();

    @Override
    public boolean equals(Object x, Object y) {
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) {
        return x.hashCode();
    }

    @Override
    public T nullSafeGet(ResultSet rs, int names, SharedSessionContractImplementor session, Object owner) throws SQLException {
        return fromDB(rs.getString(names));
    }

    @Override
    public void nullSafeSet(PreparedStatement st, T value, int index, SharedSessionContractImplementor session) throws SQLException {
        if (value == null || !returnedClass().isAssignableFrom(value.getClass())) {
            st.setNull(index, Types.VARCHAR);
        } else {
            st.setString(index, toDB(value));
        }
    }

    protected abstract T fromDB(String code);
    protected abstract String toDB(T value);

    @Override
    public T deepCopy(T value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(T value) {
        return (Serializable) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T assemble(Serializable cached, Object owner) {
        return (T)cached;
    }

    @Override
    public T replace(T original, T target, Object owner) {
        return original;
    }
}
