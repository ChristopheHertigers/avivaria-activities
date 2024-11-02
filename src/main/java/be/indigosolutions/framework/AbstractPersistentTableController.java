package be.indigosolutions.framework;

import be.avivaria.activities.model.BaseEntity;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.util.Objects;

/**
 * User: christophe
 * Date: 09/10/13
 * Time: 22:00
 */
public abstract class AbstractPersistentTableController<T extends BaseEntity> extends PersistenceController {

    protected JTable itemTable;
    protected EntityTableModel itemTableModel;
    protected T selected;
    protected boolean dirty = false;

    @SuppressWarnings("unchecked")
    public AbstractPersistentTableController(Container view, AbstractController parentController, int width, int height) {
        super(view, parentController);

        JPanel masterPanel = new JPanel();
        itemTable = new JTable() {
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < masterPanel.getWidth();
            }
        };
        itemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        itemTable.setFillsViewportHeight(true);
        refreshItemList(getPersistenceContext());
        JScrollPane tableScrollPane = new JScrollPane(itemTable);
        tableScrollPane.setPreferredSize(new Dimension(width, height));
        itemTable.setPreferredScrollableViewportSize(new Dimension(width, height));
        itemTable.setSelectionModel(new VetoableListSelectionModel() {
            @Override
            public boolean vetoSelection() {
                return dirty && !discardChanges();
            }
        });
        itemTable.getSelectionModel().addListSelectionListener(e -> {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (lsm.isSelectionEmpty()) {
                if (selected != null) {
                    clearDetail();
                }
                selected = null;
            } else {
                int index = lsm.getMinSelectionIndex();
                T aantal = (T) itemTableModel.getRow(index);
                if (selected == null || !Objects.equals(aantal.getId(), selected.getId())) {
                    selected = aantal;
                    loadDetail();
                }
            }
        });

        masterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        masterPanel.add(tableScrollPane, BorderLayout.CENTER);

        view.add(masterPanel, BorderLayout.NORTH);

        masterPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                tableScrollPane.setPreferredSize(new Dimension(masterPanel.getWidth()-30, height));
            }
        });
    }

    public abstract void refreshItemList(Session currentSession);

    protected abstract void setDirty(boolean dirty);

    protected abstract void loadDetail();

    protected abstract void clearDetail();

    protected void addItemChangeListener(final JComboBox combo) {
        combo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setDirty(true);
            }
        });
    }

    protected void addChangeListener(final JCheckBox checkBox) {
        checkBox.addChangeListener(e -> setDirty(true));
    }

    protected void addDocumentListener(final JTextField field) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                setDirty(true);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDirty(true);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDirty(true);
            }
        });
    }

    private boolean discardChanges() {
        return JOptionPane.showConfirmDialog(getView(), "Wilt u de aanpassingen annuleren?", "Opgelet", JOptionPane.OK_CANCEL_OPTION) == 0;
    }

    protected void setColumnProperties(JTable table, EntityTableModel model) {
        for (int i = 0; i < model.getColumnCount(); i++) {
            EntityTableColumn modelColumn = model.getColumn(i);
            TableColumn tableColumn = table.getColumnModel().getColumn(i);
            Integer width = modelColumn.getWidth();
            if (width != null) tableColumn.setMaxWidth(width);
            TableCellRenderer renderer = modelColumn.getCellRenderer();
            if (renderer != null) tableColumn.setCellRenderer(renderer);
            TableCellEditor editor = modelColumn.getCellEditor();
            if (editor != null) tableColumn.setCellEditor(editor);
        }
    }
}
