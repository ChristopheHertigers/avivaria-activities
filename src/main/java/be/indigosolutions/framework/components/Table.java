package be.indigosolutions.framework.components;

import be.indigosolutions.framework.celleditor.ComboBoxCellEditor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.Vector;

/**
 * @author ch
 */
public class Table extends JTable {
    private static final Logger LOGGER = LogManager.getLogger(Table.class);

    public Table() {
        super();
        initialize();
    }

    public Table(TableModel dm) {
        super(dm);
        initialize();
    }

    public Table(TableModel dm, TableColumnModel cm) {
        super(dm, cm);
        initialize();
    }

    public Table(TableModel dm, TableColumnModel cm, ListSelectionModel sm) {
        super(dm, cm, sm);
        initialize();
    }

    public Table(int numRows, int numColumns) {
        super(numRows, numColumns);
        initialize();
    }

    public Table(Vector rowData, Vector columnNames) {
        super(rowData, columnNames);
        initialize();
    }

    public Table(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        initialize();
    }

    private void initialize() {
        ActionMap am = getActionMap();
        am.put("selectPreviousColumnCell", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeSelection(getSelectedRow(), (getSelectedColumn() - 1 < 0 ? getColumnCount() - 1 : getSelectedColumn() - 1), false, false);
            }
        });
        am.put("selectNextColumnCell", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeSelection(getSelectedRow(), (getSelectedColumn() + 1 >= getColumnCount() ? 0 : getSelectedColumn() + 1), false, false);
            }
        });
    }

    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (row < 0 || column < 0) return false;
        boolean result = super.editCellAt(row, column, e);
        if (cellEditor != null) {
            if (cellEditor instanceof ComboBoxCellEditor) {
                ComboBoxCellEditor editor = (ComboBoxCellEditor) cellEditor;
                editor.registerInitialValue(row, column, getModel(), getValueAt(row, column));
                editor.getComboBox().requestFocusInWindow();
            } else if (cellEditor instanceof DefaultCellEditor) {
                ((DefaultCellEditor) cellEditor).getComponent().requestFocusInWindow();
            }
        }
        return result;
    }
}
