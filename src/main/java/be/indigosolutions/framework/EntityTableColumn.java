package be.indigosolutions.framework;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

/**
 * User: christophe
 * Date: 09/10/13
 * Time: 21:09
 */
public class EntityTableColumn {
    private String label;
    private String propertyName;
    private Integer width;
    private TableCellRenderer cellRenderer;
    private TableCellEditor cellEditor;
    private boolean editable;

    public EntityTableColumn(String label, String propertyName) {
        this(label, propertyName, null, null);
    }

    public EntityTableColumn(String label, String propertyName, Integer width) {
        this(label, propertyName, width, null);
    }

    public EntityTableColumn(String label, String propertyName, Integer width, TableCellRenderer cellRenderer) {
        this.label = label;
        this.propertyName = propertyName;
        this.width = width;
        this.cellRenderer = cellRenderer;
        this.cellEditor = null;
        this.editable = false;
    }

    public EntityTableColumn(String label, String propertyName, Integer width, TableCellEditor cellEditor, boolean editable) {
        this.label = label;
        this.propertyName = propertyName;
        this.width = width;
        this.cellRenderer = null;
        this.cellEditor = cellEditor;
        this.editable = editable;
    }

    public String getLabel() {
        return label;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public Integer getWidth() {
        return width;
    }

    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public TableCellEditor getCellEditor() {
        return cellEditor;
    }

    public boolean isEditable() {
        return editable;
    }
}
