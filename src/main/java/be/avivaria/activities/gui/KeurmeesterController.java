package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.EventRepository;
import be.avivaria.activities.dao.KeurmeesterRepository;
import be.avivaria.activities.dao.KleurRepository;
import be.avivaria.activities.dao.RasRepository;
import be.avivaria.activities.model.*;
import be.avivaria.activities.model.Event;
import be.indigosolutions.framework.AbstractTableController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.EntityTableModel;
import be.indigosolutions.framework.celleditor.AutoCompletion;
import be.indigosolutions.framework.components.Dropdown;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("FieldCanBeLocal")
@Controller
public class KeurmeesterController extends AbstractTableController<Keurmeester> {

    private static final Logger logger = LoggerFactory.getLogger(KeurmeesterController.class);

    private final MainController mainController;
    private final KeurmeesterRepository keurmeesterRepository;
    private final RasRepository rasRepository;
    private final KleurRepository kleurRepository;
    private final EventRepository eventRepository;

    private final JTextField idField;
    private final JTextField naamField;
    private final Dropdown<Ras> rasCombo;
    private final Dropdown<Kleur> kleurCombo;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    private List<Ras> rassen;
    private List<Kleur> kleuren;
    private long previousId = -1;

    @Autowired
    public KeurmeesterController(
            MainController mainController,
            KeurmeesterRepository keurmeesterRepository,
            RasRepository rasRepository,
            KleurRepository kleurRepository,
            EventRepository eventRepository
    ) {
        super(new JFrame("Keurmeesters"), 750, 113);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.mainController = mainController;
        this.keurmeesterRepository = keurmeesterRepository;
        this.rasRepository = rasRepository;
        this.kleurRepository = kleurRepository;
        this.eventRepository = eventRepository;

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Activiteit Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 4", "[r][l]40[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField, "span 3");
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(400, 30));
        naamField.setEnabled(false);
        addDocumentListener(naamField);
        detailFormPanel.add(naamField, "span 3");
        detailFormPanel.add(new JLabel("ras:"));
        rasCombo = new Dropdown<>();
        AutoCompletion.enable(rasCombo);
        rasCombo.setPreferredSize(new Dimension(400, 30));
        rasCombo.setEnabled(false);
        rasCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setDirty(true);
                refreshKleurCombo();
            }
        });
        detailFormPanel.add(rasCombo, "span 3");
        detailFormPanel.add(new JLabel("kleur:"));
        kleurCombo = new Dropdown<>();
        AutoCompletion.enable(kleurCombo);
        kleurCombo.setPreferredSize(new Dimension(400, 30));
        kleurCombo.setEnabled(false);
        addItemChangeListener(kleurCombo);
        detailFormPanel.add(kleurCombo, "span 3");


        detailPanel.add(detailFormPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new MigLayout("", "[][][grow][][][][]"));
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
        mainWindow.setMinimumSize(new Dimension(800, 700));
        mainWindow.setPreferredSize(new Dimension(800, 700));
        mainWindow.setLocation(350, 0);
    }

    @Override
    public void show() {
        refreshRelations();
        refreshItemList();
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    @SuppressWarnings("unchecked")
    private void refreshRelations() {
        rassen = rasRepository.findAllByEventOrderByNaam(getSelectedEvent());
        rasCombo.setModel(new DefaultComboBoxModel<>(rassen.toArray(new Ras[0])));
    }

    private void refreshKleurCombo() {
        Ras ras = (Ras) rasCombo.getSelectedItem();
        Optional<Kleur> noColor = kleurRepository.findById(0L);
        if (ras != null) {
            kleuren = kleurRepository.findAllByRasAndEventOrderByNaam(getSelectedEvent(), ras);
        } else {
            kleuren = kleurRepository.findAllByEventOrderByNaam(getSelectedEvent());
        }
        if (noColor.isPresent() && !kleuren.contains(noColor.get())) kleuren.add(0, noColor.get());
        //noinspection unchecked
        kleurCombo.setModel(new DefaultComboBoxModel<>(kleuren.toArray(new Kleur[0])));
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(StringUtils.isNotBlank(idField.getText()) ? Long.parseLong(idField.getText()) : null);
            selected.setNaam(naamField.getText());
            selected.setEvent(getSelectedEvent());
            selected.setRas((Ras) rasCombo.getSelectedItem());
            Kleur kleur = (Kleur) kleurCombo.getSelectedItem();
            if (kleur != null && kleur.getId() == 0L) kleur = null;
            selected.setKleur(kleur);

            try {
                keurmeesterRepository.save(selected);
            } catch (Exception e) {
                logger.error("Error while saving keurmeester", e);
                throw new RuntimeException(e);
            } finally {
                setDirty(false);
                previousId = selected.getId() == null ? -1 : selected.getId();
                refreshItemList();
            }
        }
    }

    private boolean isValid() {
        return true;
    }

    @Override
    public void refreshItemList() {
        Iterable<Keurmeester> keurmeesters = keurmeesterRepository.findAllByEventOrderByNaam(getSelectedEvent());
        itemTableModel = new EntityTableModel<>(Keurmeester.class, keurmeesters);
        itemTableModel.addColumn("Naam", "naam");
        itemTableModel.addColumn("Ras", "ras");
        itemTableModel.addColumn("Kleur", "kleur");
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Keurmeester s = (Keurmeester) itemTableModel.getRow(i);
                    if (s.getId() == previousId) {
                        itemTable.setRowSelectionInterval(i, i);
                        itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(i, 0, true)));
                        return;
                    }
                }
            }
            if (itemTable.getRowCount() > 0) {
                itemTable.setRowSelectionInterval(0, 0);
                itemTable.scrollRectToVisible(new Rectangle(itemTable.getCellRect(0, 0, true)));
            }
        });
    }

    @Override
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
        saveButton.setVisible(dirty);
        cancelButton.setVisible(dirty);
    }

    @Override
    protected void loadDetail() {
        if (selected != null) {
            idField.setText(selected.getId() != null ? ""+selected.getId() : "");
            naamField.setText(selected.getNaam());
            naamField.setEnabled(true);
            rasCombo.setSelectedItem(selected.getRas());
            rasCombo.setEnabled(true);
            refreshKleurCombo();
            kleurCombo.setSelectedItem(selected.getKleur());
            kleurCombo.setEnabled(true);
            setDirty(false);
        }
    }

    @Override
    protected void clearDetail() {
        idField.setText(null);
        naamField.setText(null);
        naamField.setEnabled(false);
        rasCombo.setSelectedItem(null);
        rasCombo.setEnabled(false);
        kleurCombo.setSelectedItem(null);
        kleurCombo.setEnabled(false);

        setDirty(false);
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
                        keurmeesterRepository.delete(selected);
                    } catch (Exception e1) {
                        logger.error("Error while deleting event",e1);
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
                refreshRelations();
                itemTable.clearSelection();
                selected = new Keurmeester();
                loadDetail();
                setDirty(true);
                SwingUtilities.invokeLater(naamField::requestFocus);
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
                if (itemTable.getRowCount() > 0) {
                    int selectedRow = itemTable.getSelectedRow() == -1 ? 0 : itemTable.getSelectedRow();
                    selected = (Keurmeester) itemTableModel.getRow(selectedRow);
                    loadDetail();
                } else {
                    clearDetail();
                }
            }
        });
        return button;
    }

    private Event getSelectedEvent() {
        return mainController.getSelectedEvent();
    }
}
