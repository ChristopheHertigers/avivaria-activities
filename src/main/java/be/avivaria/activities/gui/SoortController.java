package be.avivaria.activities.gui;

import be.avivaria.activities.dao.SoortRepository;
import be.avivaria.activities.model.Soort;
import be.indigosolutions.framework.AbstractTableController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.EntityTableModel;
import be.indigosolutions.framework.cellrenderer.CellRenderers;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;


/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:54
 */
@Controller
public class SoortController extends AbstractTableController<Soort> {
    private static final Logger logger = LoggerFactory.getLogger(SoortController.class);

    private final SoortRepository soortRepository;

    // View
    private final JTextField idField;
    private final JTextField naamField;
    private final JTextField afkortingField;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;

    @Autowired
    public SoortController(SoortRepository soortRepository) {
        super(new JFrame("Onderhoud Soorten"), 570, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.soortRepository = soortRepository;

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Soort Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 2", "[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("afkorting:"));
        afkortingField = new JTextField();
        afkortingField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(afkortingField);
        detailFormPanel.add(afkortingField);
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(naamField);
        detailFormPanel.add(naamField);

        detailPanel.add(detailFormPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new MigLayout("", "[][][grow][][][]"));
        saveButton = createSaveButton();
        buttonPanel.add(saveButton);
        cancelButton = createCancelButton();
        buttonPanel.add(cancelButton);
        buttonPanel.add(new JLabel(""));
        closeButton = createCloseButton(mainWindow);
        buttonPanel.add(closeButton, "east");
        buttonPanel.add(createDeleteButton(mainWindow), "east");
        buttonPanel.add(createNewButton(), "east");
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assemble the view
        mainWindow.getContentPane().add(detailPanel, BorderLayout.CENTER);

        // Display the window
        mainWindow.setMinimumSize(new Dimension(600, 600));
        mainWindow.setPreferredSize(new Dimension(600, 600));
        mainWindow.setLocation(350, 50);
    }

    @Override
    public void show() {
        refreshItemList();
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    @Override
    public void dispose() {
        getView().setVisible(false);
        clearDetail();
    }

    @Override
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
        saveButton.setVisible(dirty);
        cancelButton.setVisible(dirty);
    }

    @Override
    protected void clearDetail() {
        idField.setText(null);
        afkortingField.setText(null);
        naamField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        afkortingField.setText(selected.getAfkorting());
        naamField.setText(selected.getNaam());
        setDirty(false);
    }

    private boolean isValid() {
        return true;
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(StringUtils.isNotBlank(idField.getText()) ? Long.parseLong(idField.getText()) : null);
            selected.setAfkorting(afkortingField.getText());
            selected.setNaam(naamField.getText());

            try {
                soortRepository.save(selected);
            } catch (Exception e) {
                logger.error("Error saving soort", e);
                throw new RuntimeException(e);
            } finally {
                setDirty(false);
                previousId = selected.getId() == null ? -1 : selected.getId();
                refreshItemList();
            }
        }
    }

    @Override
    public void refreshItemList() {
        List<Soort> soorten = soortRepository.findAllByOrderByNaam();
        itemTableModel = new EntityTableModel<>(Soort.class, soorten);
        itemTableModel.addColumn("Afkorting", "afkorting", 60, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Naam", "naam");
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Soort s = (Soort) itemTableModel.getRow(i);
                    if (s.getId() == previousId) {
                        itemTable.setRowSelectionInterval(i,i);
                        itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(i, 0, true)));
                        return;
                    }
                }
            }
            itemTable.setRowSelectionInterval(0, 0);
            itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(0, 0, true)));
        });
    }

    private JButton createCloseButton(final JFrame parent) {
        JButton closeButton = new JButton("Sluit");
        registerAction(closeButton, new DefaultAction("close") {
            public void actionPerformed(ActionEvent e) {
                parent.setVisible(false);
                parent.dispose();
                dispose();
            }
        });
        return closeButton;
    }

    private JButton createDeleteButton(final JFrame parent) {
        JButton button = new JButton("Verwijder");
        registerAction(button, new DefaultAction("delete") {
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    try {
                        soortRepository.delete(selected);
                    } catch (Exception e1) {
                        logger.error("Error deleting soort", e1);
                        throw new RuntimeException(e1);
                    } finally {
                        setDirty(false);
                        refreshItemList();
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Er is niets geselecteerd.");
                }
            }
        });
        return button;
    }

    private JButton createNewButton() {
        JButton button = new JButton("Nieuw");
        registerAction(button, new DefaultAction("new") {
            public void actionPerformed(ActionEvent e) {
                itemTable.clearSelection();
                if (selected == null) {
                    selected = new Soort();
                    selected.setId(null);
                    idField.setText("");
                    setDirty(true);
                }

            }
        });
        return button;
    }

    private JButton createSaveButton() {
        JButton button = new JButton("Opslaan");
        button.setVisible(false);
        registerAction(button, new DefaultAction("save") {
            public void actionPerformed(ActionEvent e) {
                persistChanges();
            }
        });
        return button;
    }

    private JButton createCancelButton() {
        JButton button = new JButton("Annuleren");
        button.setVisible(false);
        registerAction(button, new DefaultAction("cancel") {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = itemTable.getSelectedRow() == -1 ? 0 : itemTable.getSelectedRow();
                selected = (Soort) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
