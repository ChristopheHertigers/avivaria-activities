package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.EventDao;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.EventType;
import be.indigosolutions.framework.*;
import be.indigosolutions.framework.cellrenderer.CellRenderers;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;


/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:54
 */
public class EventController extends AbstractPersistentTableController<Event> {
    private static final Logger LOGGER = LogManager.getLogger(EventController.class);

    public static final String LABEL_VERKOOPDAG = "Verkoopdag";
    public static final String LABEL_TENTOONSTELLING = "Tentoonstelling";

    // View
    private JTextField idField;
    private JComboBox typeCombo;
    private JTextField naamField;
    private JTextField dierenField;
    private JTextField dierenClubField;
    private JTextField dierenTeKoopField;
    private JTextField palmaresField;
    private JTextField lidgeldField;
    private JTextField lidgeldJeugdField;
    private JTextField fokkerskaartField;
    private JTextField fokkerskaart2Field;
    private JTextField clubNaamRegel1;
    private JTextField clubNaamRegel2;
    private JLabel dierenTeKoopLabel;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;

    // Model
    private long previousId = -1;

    public EventController(AbstractController parentController) {
        super(new JFrame("Activiteiten"), parentController, 600, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Activiteit Detail")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 4", "[r][l]40[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField, "span 3");
        detailFormPanel.add(new JLabel("type:"));
        typeCombo = new JComboBox(new String[]{LABEL_VERKOOPDAG, LABEL_TENTOONSTELLING});
        typeCombo.setPreferredSize(new Dimension(200, 30));
        typeCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    setDirty(true);
                    updateDierenTeKoopLabel((String)e.getItem());
                }
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
        mainWindow.setVisible(true);

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
    protected void doDispose() {
        getView().setVisible(false);
        idField = null;
        typeCombo = null;
        naamField = null;
        dierenField = null;
        dierenClubField = null;
        dierenTeKoopField = null;
        palmaresField = null;
        lidgeldField = null;
        lidgeldJeugdField = null;
        fokkerskaartField = null;
        fokkerskaart2Field = null;
        clubNaamRegel1 = null;
        clubNaamRegel2 = null;
        saveButton = null;
        cancelButton = null;
        ControllerRegistry.getInstance().unregister(this);
        ((JFrame)getView()).dispose();
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
        idField.setText("" + selected.getId());
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
        clubNaamRegel1.setText(""+selected.getClubNaamRegel1());
        clubNaamRegel2.setText(""+selected.getClubNaamRegel2());
        updateDierenTeKoopLabel((String)typeCombo.getSelectedItem());
        setDirty(false);
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(idField.getText())) return false;
        try {
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

    private void persistChanges() {
        if (isValid()) {
            selected.setId(Long.parseLong(idField.getText()));
            selected.setType(LABEL_VERKOOPDAG.equals(typeCombo.getSelectedItem()) ? EventType.Verkoopdag : EventType.Tentoonstelling);
            selected.setNaam(naamField.getText());
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

            Session session = getPersistenceContext();
            EventDao eventDao = new EventDao(session);
            Transaction transaction = session.getTransaction();
            try {
                transaction.begin();
                eventDao.saveOrUpdate(selected);
                eventDao.flush();
                transaction.commit();
            } catch (Exception e1) {
                transaction.rollback();
                throw new RuntimeException(e1);
            } finally {
                setDirty(false);
                previousId = selected.getId();
                refreshItemList(session);
            }
        }
    }

    @SuppressWarnings("unchecked") @Override
    public void refreshItemList(Session currentSession) {
        EventDao eventDao = new EventDao(currentSession);
        List<Event> events = eventDao.findAll();
        itemTableModel = new EntityTableModel<>(Event.class, events);
        itemTableModel.addColumn("Type", "type.code", 50, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Naam", "naam");
        itemTableModel.addColumn("Actief", "selected", 60, CellRenderers.BooleanCentered.getRenderer());
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
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
            }
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
                    Session session = getPersistenceContext();
                    EventDao eventDao = new EventDao(session);
                    Transaction transaction = session.getTransaction();
                    try {
                        transaction.begin();
                        eventDao.delete(selected);
                        eventDao.flush();
                        transaction.commit();
                    } catch (Exception e1) {
                        transaction.rollback();
                        throw new RuntimeException(e1);
                    } finally {
                        setDirty(false);
                        refreshItemList(session);
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
                    Session session = getPersistenceContext();
                    EventDao eventDao = new EventDao(session);
                    long nextId = eventDao.getNextId();
                    selected = new Event();
                    selected.setId(nextId);
                    selected.setSelected(false);
                    idField.setText(""+nextId);
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
                    final Session session = getPersistenceContext();
                    EventDao eventDao = new EventDao(session);
                    Transaction transaction = session.getTransaction();
                    try {
                        transaction.begin();
                        eventDao.selectEvent(selected);
                        eventDao.flush();
                        transaction.commit();
                    } catch (Exception e1) {
                        transaction.rollback();
                        throw new RuntimeException(e1);
                    } finally {
                        setDirty(false);
                        previousId = selected.getId();
                        SwingUtilities.invokeLater(() -> {
                            refreshItemList(session);
                            ((MainController)getParentController()).loadData();
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
