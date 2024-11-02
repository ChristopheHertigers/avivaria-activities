package be.avivaria.activities.gui;

import be.avivaria.activities.dao.AantalRepository;
import be.avivaria.activities.model.Aantal;
import be.indigosolutions.framework.AbstractTableController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.EntityTableModel;
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
public class AantalController extends AbstractTableController<Aantal> {
    private static final Logger logger = LoggerFactory.getLogger(AantalController.class);

    private final AantalRepository aantalRepository;

    // View
    private final JTextField idField;
    private final JTextField naamField;
    private final JTextField aantalField;
    private final JTextField aantal2Field;
    private final JTextField benamingField;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;

    @Autowired
    public AantalController(AantalRepository aantalRepository) {
        super(new JFrame("Onderhoud Aantallen"), 570, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.aantalRepository = aantalRepository;

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Aantal Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 2", "[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(naamField);
        detailFormPanel.add(naamField);
        detailFormPanel.add(new JLabel("aantal (hok):"));
        aantalField = new JTextField();
        aantalField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(aantalField);
        detailFormPanel.add(aantalField);
        detailFormPanel.add(new JLabel("aantal (dier):"));
        aantal2Field = new JTextField();
        aantal2Field.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(aantal2Field);
        detailFormPanel.add(aantal2Field);
        detailFormPanel.add(new JLabel("benaming:"));
        benamingField = new JTextField();
        benamingField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(benamingField);
        detailFormPanel.add(benamingField);

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
        naamField.setText(null);
        aantalField.setText(null);
        aantal2Field.setText(null);
        benamingField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        naamField.setText(selected.getNaam());
        aantalField.setText("" + selected.getAantal());
        aantal2Field.setText("" + selected.getAantal2());
        benamingField.setText(selected.getBenaming());
        setDirty(false);
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(aantalField.getText())) return false;
        return StringUtils.isNumeric(aantal2Field.getText());
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(StringUtils.isNotBlank(idField.getText()) ? Long.parseLong(idField.getText()) : null);
            selected.setNaam(naamField.getText());
            selected.setAantal(Integer.parseInt(aantalField.getText()));
            selected.setAantal2(Integer.parseInt(aantal2Field.getText()));
            selected.setBenaming(benamingField.getText());

            try {
                aantalRepository.save(selected);
            } catch (Exception e1) {
                logger.error("Error saving aantal", e1);
                throw new RuntimeException(e1);
            } finally {
                setDirty(false);
                previousId = selected.getId() == null ? -1 : selected.getId();
                refreshItemList();
            }
        }
    }

    @Override
    public void refreshItemList() {
        List<Aantal> aantallen = aantalRepository.findAllByOrderByNaam();
        itemTableModel = new EntityTableModel<>(Aantal.class, aantallen);
        itemTableModel.addColumn("Naam", "naam");
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Aantal a = (Aantal) itemTableModel.getRow(i);
                    if (a.getId() == previousId) {
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
                        aantalRepository.delete(selected);
                    } catch (Exception e1) {
                        logger.error("Error deleting aantal", e1);
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
                    selected = new Aantal();
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
                selected = (Aantal) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
