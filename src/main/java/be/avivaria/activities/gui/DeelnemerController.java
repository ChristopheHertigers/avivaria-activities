package be.avivaria.activities.gui;

import be.avivaria.activities.dao.DeelnemerDao;
import be.avivaria.activities.dao.VerenigingDao;
import be.avivaria.activities.model.Deelnemer;
import be.avivaria.activities.model.Vereniging;
import be.indigosolutions.framework.*;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
public class DeelnemerController extends AbstractPersistentTableController<Deelnemer> {
    private static final Logger LOGGER = LogManager.getLogger(DeelnemerController.class);

    // View
    private JTextField idField;
    private JTextField naamField;
    private JTextField straatField;
    private JTextField woonplaatsField;
    private JTextField telefoonField;
    private JComboBox verenigingCombo;
    private JTextField fokkerskaartField;
    private JTextField jeugdDeelnemerField;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;

    // Model
    private long previousId = -1;

    public DeelnemerController(AbstractController parentController) {
        super(new JFrame("Deelnemers"), parentController, 570, 250);
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
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Deelnemer Detail")));

        List<Vereniging> verenigingen = new VerenigingDao(getPersistenceContext()).findAll();

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 2", "[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("naam:"));
        naamField = new JTextField();
        naamField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(naamField);
        detailFormPanel.add(naamField);
        detailFormPanel.add(new JLabel("straat:"));
        straatField = new JTextField();
        straatField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(straatField);
        detailFormPanel.add(straatField);
        detailFormPanel.add(new JLabel("woonplaats:"));
        woonplaatsField = new JTextField();
        woonplaatsField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(woonplaatsField);
        detailFormPanel.add(woonplaatsField);
        detailFormPanel.add(new JLabel("telefoon:"));
        telefoonField = new JTextField();
        telefoonField.setPreferredSize(new Dimension(200, 30));
        addDocumentListener(telefoonField);
        detailFormPanel.add(telefoonField);
        detailFormPanel.add(new JLabel("vereniging:"));
        verenigingCombo = new JComboBox(verenigingen.toArray(new Vereniging[verenigingen.size()]));
        verenigingCombo.setPreferredSize(new Dimension(400, 30));
        addItemChangeListener(verenigingCombo);
        detailFormPanel.add(verenigingCombo);
        detailFormPanel.add(new JLabel("fokkerskaart:"));
        fokkerskaartField = new JTextField();
        fokkerskaartField.setPreferredSize(new Dimension(200, 30));
        addDocumentListener(fokkerskaartField);
        detailFormPanel.add(fokkerskaartField);
        detailFormPanel.add(new JLabel("jeugddeelnemer:"));
        jeugdDeelnemerField = new JTextField();
        jeugdDeelnemerField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(jeugdDeelnemerField);
        detailFormPanel.add(jeugdDeelnemerField);

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
        mainWindow.setMinimumSize(new Dimension(600, 700));
        mainWindow.setPreferredSize(new Dimension(600, 700));
        mainWindow.setLocation(350, 50);
        mainWindow.setVisible(true);

        // initial display
        closeButton.requestFocus();
    }

    @Override
    protected void doDispose() {
        getView().setVisible(false);
        idField = null;
        naamField = null;
        straatField = null;
        woonplaatsField = null;
        telefoonField = null;
        verenigingCombo = null;
        fokkerskaartField = null;
        jeugdDeelnemerField = null;
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
        naamField.setText(null);
        straatField.setText(null);
        woonplaatsField.setText(null);
        telefoonField.setText(null);
        verenigingCombo.setSelectedItem(null);
        fokkerskaartField.setText(null);
        jeugdDeelnemerField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        naamField.setText(selected.getNaam());
        straatField.setText(selected.getStraat());
        woonplaatsField.setText(selected.getWoonplaats());
        telefoonField.setText(selected.getTelefoon());
        verenigingCombo.setSelectedItem(selected.getVereniging());
        fokkerskaartField.setText(selected.getFokkerskaartNummer());
        jeugdDeelnemerField.setText(""+(selected.getJeugddeelnemer() == null ? "" : selected.getJeugddeelnemer()));
        setDirty(false);
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(idField.getText())) return false;
        if (StringUtils.isNotBlank(jeugdDeelnemerField.getText()) && !StringUtils.isNumeric(jeugdDeelnemerField.getText())) return false;
        return true;
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(Long.parseLong(idField.getText()));
            selected.setNaam(naamField.getText());
            selected.setStraat(straatField.getText());
            selected.setWoonplaats(woonplaatsField.getText());
            selected.setTelefoon(telefoonField.getText());
            selected.setVereniging((Vereniging) verenigingCombo.getSelectedItem());
            selected.setFokkerskaartNummer(fokkerskaartField.getText());
            selected.setJeugddeelnemer(StringUtils.isBlank(jeugdDeelnemerField.getText()) ? null : Integer.parseInt(jeugdDeelnemerField.getText()));

            Session session = getPersistenceContext();
            DeelnemerDao deelnemerDao = new DeelnemerDao(session);
            Transaction transaction = session.getTransaction();
            try {
                transaction.begin();
                deelnemerDao.saveOrUpdate(selected);
                deelnemerDao.flush();
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

    @SuppressWarnings("unchecked")
    public void refreshItemList(Session currentSession) {
        DeelnemerDao deelnemerDao = new DeelnemerDao(currentSession);
        List<Deelnemer> deelnemers = deelnemerDao.findAll();
        itemTableModel = new EntityTableModel<>(Deelnemer.class, deelnemers);
        itemTableModel.addColumn("Naam", "naam", 420);
        itemTableModel.addColumn("Telefoon", "telefoon", 150);
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (previousId >= 0) {
                    for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                        Deelnemer s = (Deelnemer) itemTableModel.getRow(i);
                        if (s.getId() == previousId) {
                            itemTable.setRowSelectionInterval(i,i);
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
                    Transaction transaction = session.getTransaction();
                    try {
                        transaction.begin();
                        DeelnemerDao deelnemerDao = new DeelnemerDao(session);
                        deelnemerDao.delete(selected);
                        deelnemerDao.flush();
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
                    DeelnemerDao deelnemerDao = new DeelnemerDao(session);
                    long nextId = deelnemerDao.getNextId();
                    selected = new Deelnemer();
                    selected.setId(nextId);
                    idField.setText(""+nextId);
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
                selected = (Deelnemer) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
