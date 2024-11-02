package be.indigosolutions.framework;


import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.lang.reflect.Method;
import java.beans.PropertyDescriptor;
import java.util.Objects;

/**
 * A table model for Swing that works easily with rows of JavaBeans.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public class EntityTableModel<T> extends AbstractTableModel {

    private Class<T> entityClass;
    private List<EntityTableColumn> columns = new ArrayList<EntityTableColumn>();
    private List<T> rows;

    public EntityTableModel(Class<T> entityClass, Collection<T> rows) {
        this.entityClass = entityClass;
        this.rows = new ArrayList<T>(rows);
    }

    public String getColumnName(int column) {
        return columns.get(column).getLabel();
    }

    public int getColumnCount() {
        return columns.size();
    }

    public int getRowCount() {
        return rows.size();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return columns.get(column).isEditable();
    }

    public Object getValueAt(int row, int column) {
        Object value = null;
        T entityInstance = rows.get(row);
        if (entityInstance != null) {
            EntityTableColumn col = columns.get(column);
            value = read(col, entityInstance);
        }
        return value;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (rowIndex < rows.size()) {
            T row = getRow(rowIndex);
            if (row != null) {
                EntityTableColumn col = columns.get(columnIndex);
                write(col, row, value, rowIndex, columnIndex);
            }
        }
    }

    private Object read(EntityTableColumn col, T instance) {
        Object value = instance;
        try {
            String nestedPropertyName = col.getPropertyName();
            String[] splitPropertyName = nestedPropertyName.split("\\.");
            Class clazz;
            for (String propertyName : splitPropertyName) {
                clazz = value == null ? entityClass : value.getClass();
                PropertyDescriptor property = new PropertyDescriptor(propertyName, clazz);
                Method readMethod = property.getReadMethod();
                value = readMethod.invoke(value);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return value;
    }

    private void write(EntityTableColumn col, T instance, Object newValue, int rowNbr, int colNbr) {
        Object value = instance;
        try {
            String nestedPropertyName = col.getPropertyName();
            String[] splitPropertyName = nestedPropertyName.split("\\.");
            Class clazz;
            int length = splitPropertyName.length;
            for (int i = 1; i < length; i++) {
                String propertyName = splitPropertyName[i];
                clazz = value == null ? entityClass : value.getClass();
                PropertyDescriptor property = new PropertyDescriptor(propertyName, clazz, propertyName, null);
                Method readMethod = property.getReadMethod();
                value = readMethod.invoke(value);
            }
            clazz = value == null ? entityClass : value.getClass();
            PropertyDescriptor property = new PropertyDescriptor(splitPropertyName[length - 1], clazz);
            Method writeMethod = property.getWriteMethod();
            Class argumentClass = writeMethod.getParameterTypes()[0];
            Object newValueToSet = convert(newValue, argumentClass);
            Object currentValue = read(col, instance);
            if (changed(currentValue, newValueToSet)) {
                writeMethod.invoke(value, newValueToSet);
                fireTableCellUpdated(rowNbr, colNbr);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private boolean changed(Object origValue, Object newValue) {
        return !(origValue == null && newValue == null) && !Objects.equals(origValue, newValue);
    }

    @SuppressWarnings("unchecked")
    private Object convert(Object value, Class expectedClass) {
        if (value == null) return null;
        if (value instanceof String) {
            String stringValue = (String) value;
            if (StringUtils.isBlank(stringValue)) {
                return null;
            } else if (expectedClass.isAssignableFrom(String.class)) {
                return value;
            } else if (expectedClass.isAssignableFrom(Integer.class)) {
                return Integer.parseInt(stringValue);
            } else if (expectedClass.isAssignableFrom(Float.class)) {
                return Float.parseFloat(supportComma(stringValue));
            } else if (expectedClass.isAssignableFrom(Long.class)) {
                return Long.parseLong(stringValue);
            } else if (expectedClass.isAssignableFrom(Double.class)) {
                return Double.parseDouble(supportComma(stringValue));
            } else if (expectedClass.isAssignableFrom(Boolean.class)) {
                return BooleanUtils.toBoolean(stringValue);
            }
        }
        if (value.getClass().equals(expectedClass)) return value;
        throw new RuntimeException();
    }

    public String supportComma(String value) {
        if (StringUtils.isBlank(value)) return value;
        return value.replace(",", ".");
    }

    public void addColumn(String displayName, String propertyName) {
        columns.add(new EntityTableColumn(displayName, propertyName));
    }

    public void addColumn(String displayName, String propertyName, int width) {
        columns.add(new EntityTableColumn(displayName, propertyName, width));
    }

    public void addColumn(String displayName, String propertyName, int width, TableCellRenderer renderer) {
        columns.add(new EntityTableColumn(displayName, propertyName, width, renderer));
    }

    public void addColumn(String displayName, String propertyName, int width, TableCellEditor editor, boolean editable) {
        columns.add(new EntityTableColumn(displayName, propertyName, width, editor, editable));
    }

    public void resetColumns() {
        columns = new ArrayList<EntityTableColumn>();
    }

    public void setRow(int row, T entityInstance) {
        rows.remove(row);
        rows.add(row, entityInstance);
        fireTableDataChanged();
    }

    public T getRow(int row) {
        return rows.get(row);
    }

    public void addRow(T entityInstance) {
        rows.add(entityInstance);
        fireTableDataChanged();
    }

    public void addRow(int row, T entityInstance) {
        rows.add(row, entityInstance);
        fireTableDataChanged();
    }

    public void removeRow(int row) {
        rows.remove(row);
        fireTableDataChanged();
    }

    public void removeRow(T entityInstance) {
        rows.remove(entityInstance);
        fireTableDataChanged();
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(Collection<T> rows) {
        this.rows = new ArrayList<T>(rows);
        fireTableDataChanged();
    }

    public List<EntityTableColumn> getColumns() {
        return columns;
    }

    public EntityTableColumn getColumn(int i) {
        if (columns == null || i >= columns.size()) return null;
        return columns.get(i);
    }

}