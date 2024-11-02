package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.EventRepository;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.EventType;
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
import java.awt.event.*;


/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:54
 */
@Controller
public class EventController extends AbstractTableController<Event> {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    public static final String LABEL_VERKOOPDAG = "Verkoopdag";
    public static final String LABEL_TENTOONSTELLING = "Tentoonstelling";

    private final MainController mainController;
    private final EventRepository eventRepository;

    // View
    private final JTextField idField;
    private final JComboBox<String> typeCombo;
    private final JTextField naamField;
    private final JTextField dierenField;
    private final JTextField hokStartField;
    private final JTextField dierenClubField;
    private final JTextField dierenTeKoopField;
    private final JTextField palmaresField;
    private final JTextField lidgeldField;
    private final JTextField lidgeldJeugdField;
    private final JTextField fokkerskaartField;
    private final JTextField fokkerskaart2Field;
    private final JTextField clubNaamRegel1;
    private final JTextField clubNaamRegel2;
    private final JLabel dierenTeKoopLabel;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;

    @Autowired
    public EventController(MainController mainController, EventRepository eventRepository) {
        super(new JFrame("Activiteiten"), 600, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.mainController = mainController;
        this.eventRepository = eventRepository;

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Activiteit Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 4", "[r][l]40[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("1ste hoknr:"));
        hokStartField = new JTextField();
        hokStartField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(hokStartField);
        detailFormPanel.add(hokStartField);
        detailFormPanel.add(new JLabel("type:"));
        typeCombo = new JComboBox<>(new String[]{LABEL_VERKOOPDAG, LABEL_TENTOONSTELLING});
        typeCombo.setPreferredSize(new Dimension(200, 30));
        typeCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setDirty(true);
                updateDierenTeKoopLabel((String)e.getItem());
            }
        });
        detailFormPanel.add(typeCombo, "span 3");
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(naamField);
        detailFormPanel.add(naamField, "span 3");
        detailFormPanel.add(new JLabel("club:"));
        clubNaamRegel1 = new JTextField();
        clubNaamRegel1.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(clubNaamRegel1);
        detailFormPanel.add(clubNaamRegel1, "span 3");
        detailFormPanel.add(new JLabel(""));
        clubNaamRegel2 = new JTextField();
        clubNaamRegel2.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(clubNaamRegel2);
        detailFormPanel.add(clubNaamRegel2, "span 3");
        detailFormPanel.add(new JLabel("dieren:"));
        dierenField = new JTextField();
        dierenField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(dierenField);
        detailFormPanel.add(dierenField);
        detailFormPanel.add(new JLabel("lidgeld:"));
        lidgeldField = new JTextField();
        lidgeldField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(lidgeldField);
        detailFormPanel.add(lidgeldField);
        detailFormPanel.add(new JLabel("dieren club:"));
        dierenClubField = new JTextField();
        dierenClubField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(dierenClubField);
        detailFormPanel.add(dierenClubField);
        detailFormPanel.add(new JLabel("lidgeld jeugd:"));
        lidgeldJeugdField = new JTextField();
        lidgeldJeugdField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(lidgeldJeugdField);
        detailFormPanel.add(lidgeldJeugdField);
        dierenTeKoopLabel = new JLabel("dieren te koop:");
        detailFormPanel.add(dierenTeKoopLabel);
        dierenTeKoopField = new JTextField();
        dierenTeKoopField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(dierenTeKoopField);
        detailFormPanel.add(dierenTeKoopField);
        detailFormPanel.add(new JLabel("fokkerskaart:"));
        fokkerskaartField = new JTextField();
        fokkerskaartField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(fokkerskaartField);
        detailFormPanel.add(fokkerskaartField);
        detailFormPanel.add(new JLabel("palmares:"));
        palmaresField = new JTextField();
        palmaresField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(palmaresField);
        detailFormPanel.add(palmaresField);
        detailFormPanel.add(new JLabel("fokkerskaart 2:"));
        fokkerskaart2Field = new JTextField();
        fokkerskaart2Field.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(fokkerskaart2Field);
        detailFormPanel.add(fokkerskaart2Field);

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
        buttonPanel.add(createSelectButton(mainWindow), "east");
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assemble the view
        mainWindow.getContentPane().add(detailPanel, BorderLayout.CENTER);

        // Display the window
        mainWindow.setMinimumSize(new Dimension(630, 700));
        mainWindow.setPreferredSize(new Dimension(630, 700));
        mainWindow.setLocation(350, 50);
    }


    @Override
    public void show() {
        refreshItemList();
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    private void updateDierenTeKoopLabel(String label) {
        if (LABEL_VERKOOPDAG.equals(label)) {
            dierenTeKoopLabel.setText("hokprijs:");
        } else {
            dierenTeKoopLabel.setText("dieren te koop:");
        }
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
        hokStartField.setText(null);
        typeCombo.setSelectedItem(null);
        naamField.setText(null);
        dierenField.setText(null);
        dierenClubField.setText(null);
        dierenTeKoopField.setText(null);
        palmaresField.setText(null);
        lidgeldField.setText(null);
        lidgeldJeugdField.setText(null);
        fokkerskaartField.setText(null);
        fokkerskaart2Field.setText(null);
        clubNaamRegel1.setText("Kleindierclub");
        clubNaamRegel2.setText("AVIVARIA VZW.");
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        if (selected != null) {
            idField.setText(selected.getId() != null ? ""+selected.getId() : "");
            hokStartField.setText(selected.getHokStartNummer() != null ? ""+selected.getHokStartNummer() : "1");
            typeCombo.setSelectedItem(EventType.Verkoopdag == selected.getType() ? LABEL_VERKOOPDAG : LABEL_TENTOONSTELLING);
            naamField.setText(selected.getNaam());
            dierenField.setText("" + selected.getPrijsDier());
            dierenClubField.setText("" + selected.getPrijsDierClub());
            dierenTeKoopField.setText("" + selected.getPrijsDierTeKoop());
            palmaresField.setText(""+selected.getPrijsPalmares());
            lidgeldField.setText(""+selected.getPrijsLidgeld());
            lidgeldJeugdField.setText(""+selected.getPrijsLidgeldJeugd());
            fokkerskaartField.setText(""+selected.getPrijsFokkerskaart());
            fokkerskaart2Field.setText(""+selected.getPrijsFokkerskaart2());
            clubNaamRegel1.setText(selected.getClubNaamRegel1());
            clubNaamRegel2.setText(selected.getClubNaamRegel2());
            updateDierenTeKoopLabel((String)typeCombo.getSelectedItem());
            setDirty(false);
        }
    }

    private boolean isValid() {
        try {
            parseInt(hokStartField.getText());
            parseDouble(dierenField.getText());
            parseDouble(dierenClubField.getText());
            parseDouble(dierenTeKoopField.getText());
            parseDouble(palmaresField.getText());
            parseDouble(lidgeldField.getText());
            parseDouble(lidgeldJeugdField.getText());
            parseDouble(fokkerskaartField.getText());
            parseDouble(fokkerskaart2Field.getText());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private Double parseDouble(String value) {
        if (StringUtils.isBlank(value)) return 0.0;
        return Double.parseDouble(value.trim().replaceAll(",", "."));
    }
    private Integer parseInt(String value) {
        return parseInt(value, 0);
    }

    private Integer parseInt(String value, int defaultValue) {
        if (StringUtils.isBlank(value)) return defaultValue;
        return Integer.parseInt(value.trim());
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(StringUtils.isNotBlank(idField.getText()) ? Long.parseLong(idField.getText()) : null);
            selected.setType(LABEL_VERKOOPDAG.equals(typeCombo.getSelectedItem()) ? EventType.Verkoopdag : EventType.Tentoonstelling);
            selected.setNaam(naamField.getText());
            selected.setHokStartNummer(parseInt(hokStartField.getText(), 1));
            selected.setPrijsDier(parseDouble(dierenField.getText()));
            selected.setPrijsDierClub(parseDouble(dierenClubField.getText()));
            selected.setPrijsDierTeKoop(parseDouble(dierenTeKoopField.getText()));
            selected.setPrijsPalmares(parseDouble(palmaresField.getText()));
            selected.setPrijsLidgeld(parseDouble(lidgeldField.getText()));
            selected.setPrijsLidgeldJeugd(parseDouble(lidgeldJeugdField.getText()));
            selected.setPrijsFokkerskaart(parseDouble(fokkerskaartField.getText()));
            selected.setPrijsFokkerskaart2(parseDouble(fokkerskaart2Field.getText()));
            selected.setClubNaamRegel1(clubNaamRegel1.getText());
            selected.setClubNaamRegel2(clubNaamRegel2.getText());

            try {
                eventRepository.save(selected);
            } catch (Exception e) {
                logger.error("Error while saving event", e);
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
        Iterable<Event> events = eventRepository.findAllByOrderByIdDesc();
        itemTableModel = new EntityTableModel<>(Event.class, events);
        itemTableModel.addColumn("Type", "type.code", 50, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Naam", "naam");
        itemTableModel.addColumn("Actief", "selected", 60, CellRenderers.BooleanCentered.getRenderer());
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Event s = (Event) itemTableModel.getRow(i);
                    if (s.getId() == previousId) {
                        itemTable.setRowSelectionInterval(i, i);
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
                mainController.loadData();
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
                        eventRepository.delete(selected);
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
                itemTable.clearSelection();
                if (selected == null) {
                    selected = new Event();
                    selected.setSelected(false);
                    setDirty(true);
                }

            }
        });
        return button;
    }

    private JButton createSelectButton(final JFrame parent) {
        JButton button = new JButton("Selecteer");
        registerAction(button, new DefaultAction("select") {
            public void actionPerformed(ActionEvent e) {
                if (selected != null) {
                    try {
                        eventRepository.deselectAll();
                        eventRepository.select(selected.getId());
                    } catch (Exception e1) {
                        logger.error("Error selecting event", e1);
                        throw new RuntimeException(e1);
                    } finally {
                        setDirty(false);
                        previousId = selected.getId() == null ? -1 : selected.getId();
                        SwingUtilities.invokeLater(() -> {
                            refreshItemList();
                            mainController.loadData();
                        });
                    }
                } else {
                    JOptionPane.showMessageDialog(parent, "Er is niets geselecteerd.");
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
                selected = (Event) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
