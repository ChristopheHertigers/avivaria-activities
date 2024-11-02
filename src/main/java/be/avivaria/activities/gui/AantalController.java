package be.avivaria.activities.gui;

import be.avivaria.activities.dao.AantalDao;
import be.avivaria.activities.model.Aantal;
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
public class AantalController extends AbstractPersistentTableController<Aantal> {
    private static final Logger LOGGER = LogManager.getLogger(AantalController.class);

    // View
    private JTextField idField;
    private JTextField naamField;
    private JTextField aantalField;
    private JTextField benamingField;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;

    // Model
    private long previousId = -1;

    public AantalController(AbstractController parentController) {
        super(new JFrame("Onderhoud Aantallen"), parentController, 570, 250);
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
        detailFormPanel.add(new JLabel("aantal:"));
        aantalField = new JTextField();
        aantalField.setPreferredSize(new Dimension(100, 30));
        addDocumentListener(aantalField);
        detailFormPanel.add(aantalField);
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
        mainWindow.setVisible(true);

        // initial display
        closeButton.requestFocus();
    }

    @Override
    protected void doDispose() {
        getView().setVisible(false);
        idField = null;
        naamField = null;
        aantalField = null;
        benamingField = null;
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
        aantalField.setText(null);
        benamingField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        naamField.setText(selected.getNaam());
        aantalField.setText("" + selected.getAantal());
        benamingField.setText(selected.getBenaming());
        setDirty(false);
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(idField.getText())) return false;
        if (!StringUtils.isNumeric(aantalField.getText())) return false;
        return true;
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(Long.parseLong(idField.getText()));
            selected.setNaam(naamField.getText());
            selected.setAantal(Integer.parseInt(aantalField.getText()));
            selected.setBenaming(benamingField.getText());

            Session session = getPersistenceContext();
            Transaction transaction = session.getTransaction();
            try {
                transaction.begin();
                AantalDao aantalDao = new AantalDao(session);
                aantalDao.saveOrUpdate(selected);
                aantalDao.flush();
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
        AantalDao aantalDao = new AantalDao(currentSession);
        List<Aantal> aantallen = aantalDao.findAll();
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
                    Session session = getPersistenceContext();
                    Transaction transaction = session.getTransaction();
                    try {
                        transaction.begin();
                        AantalDao aantalDao = new AantalDao(session);
                        aantalDao.delete(selected);
                        aantalDao.flush();
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
                    AantalDao aantalDao = new AantalDao(session);
                    long nextId = aantalDao.getNextId();
                    selected = new Aantal();
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
                selected = (Aantal) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }
}
