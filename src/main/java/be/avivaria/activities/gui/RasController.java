package be.avivaria.activities.gui;

import be.avivaria.activities.dao.RasRepository;
import be.avivaria.activities.dao.SoortRepository;
import be.avivaria.activities.model.HokType;
import be.avivaria.activities.model.Ras;
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
@SuppressWarnings("FieldCanBeLocal")
@Controller
public class RasController extends AbstractTableController<Ras> {
    private static final Logger logger = LoggerFactory.getLogger(RasController.class);

    private final RasRepository rasRepository;
    private final SoortRepository soortRepository;

    // View
    private final JTextField idField;
    private final JComboBox<Soort> soortCombo;
    private final JTextField naamField;
    private final JTextField hokTypeManField;
    private final JTextField hokTypeVrouwField;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;
    private List<Soort> soorten;

    @Autowired
    public RasController(RasRepository rasRepository, SoortRepository soortRepository) {
        super(new JFrame("Onderhoud Rassen"), 570, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.rasRepository = rasRepository;
        this.soortRepository = soortRepository;

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Ras Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 2", "[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("soort:"));
        soortCombo = new JComboBox<>();
        soortCombo.setPreferredSize(new Dimension(400, 30));
        addItemChangeListener(soortCombo);
        detailFormPanel.add(soortCombo);
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(naamField);
        detailFormPanel.add(naamField);
        detailFormPanel.add(new JLabel("hoktype man:"));
        hokTypeManField = new JTextField();
        hokTypeManField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(hokTypeManField);
        detailFormPanel.add(hokTypeManField);
        detailFormPanel.add(new JLabel("hoktype vrouw:"));
        hokTypeVrouwField = new JTextField();
        hokTypeVrouwField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(hokTypeVrouwField);
        detailFormPanel.add(hokTypeVrouwField);

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
        refreshRelations();
        refreshItemList();
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    private void refreshRelations() {
        soorten = soortRepository.findAllByOrderByNaam();
        soortCombo.setModel(new DefaultComboBoxModel<>(soorten.toArray(new Soort[0])));
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
        soortCombo.setSelectedItem(null);
        hokTypeManField.setText(null);
        hokTypeVrouwField.setText(null);
        naamField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        soortCombo.setSelectedItem(selected.getSoort());
        hokTypeManField.setText(""+selected.getHokTypeMan().getType());
        hokTypeVrouwField.setText(""+selected.getHokTypeVrouw().getType());
        naamField.setText(selected.getNaam());
        setDirty(false);
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(hokTypeManField.getText())) return false;
        return StringUtils.isNumeric(hokTypeVrouwField.getText());
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(StringUtils.isNotBlank(idField.getText()) ? Long.parseLong(idField.getText()) : null);
            selected.setSoort((Soort) soortCombo.getSelectedItem());
            selected.setHokTypeMan(HokType.fromType(Integer.parseInt(hokTypeManField.getText())));
            selected.setHokTypeVrouw(HokType.fromType(Integer.parseInt(hokTypeVrouwField.getText())));
            selected.setNaam(naamField.getText());
            selected.setBelgisch(false);
            selected.setVolgorde(1000);

            try {
                rasRepository.save(selected);
            } catch (Exception e) {
                logger.error("Error saving ras", e);
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
        List<Ras> rassen = rasRepository.findAllByOrderByNaam();
        itemTableModel = new EntityTableModel<>(Ras.class, rassen);
        itemTableModel.addColumn("Soort", "soort");
        itemTableModel.addColumn("Naam", "naam");
        itemTableModel.addColumn("Hok M", "hokTypeMan.type", 50, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Hok V", "hokTypeVrouw.type", 50, CellRenderers.StringCentered.getRenderer());
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Ras s = (Ras) itemTableModel.getRow(i);
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
                        rasRepository.delete(selected);
                    } catch (Exception e1) {
                        logger.error("Error deleting ras", e1);
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
                    selected = new Ras();
                    selected.setId(null);
                    selected.setErkend(false);
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
                selected = (Ras) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
