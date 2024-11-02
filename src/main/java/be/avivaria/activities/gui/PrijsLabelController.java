package be.avivaria.activities.gui;

import be.avivaria.activities.dao.EventDao;
import be.avivaria.activities.dao.HokDao;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.indigosolutions.framework.*;
import be.indigosolutions.framework.cellrenderer.CellRenderers;
import net.miginfocom.swing.MigLayout;
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
@SuppressWarnings("Duplicates")
public class PrijsLabelController extends AbstractPersistentTableController<Hok> {
    private static final Logger LOGGER = LogManager.getLogger(PrijsLabelController.class);

    // View
    private JTextField idField;
    private JTextField hoknummerField;
    private JTextField aantalField;
    private JTextField rasField;
    private JTextField kleurField;
    private JTextField deelnemerField;
    private JTextField prijsField;
    private JTextField opmerkingField;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;

    // Model
    private long previousId = -1;
    private Event selectedEvent;

    public PrijsLabelController(AbstractController parentController) {
        super(new JFrame("Prijs Labels"), parentController, 570, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });
        getSelectedEvent();

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Hok")));

        JPanel detailFormPanel = new JPanel(new MigLayout("wrap 2", "[r][l]"));
        detailFormPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        detailFormPanel.add(idField);
        detailFormPanel.add(new JLabel("hoknummer:"));
        hoknummerField = new JTextField();
        hoknummerField.setPreferredSize(new Dimension(100, 30));
        hoknummerField.setEnabled(false);
        detailFormPanel.add(hoknummerField);
        detailFormPanel.add(new JLabel("aantal:"));
        aantalField = new JTextField();
        aantalField.setPreferredSize(new Dimension(100, 30));
        aantalField.setEnabled(false);
        detailFormPanel.add(aantalField);
        detailFormPanel.add(new JLabel("ras:"));
        rasField = new JTextField();
        rasField.setPreferredSize(new Dimension(400, 30));
        rasField.setEnabled(false);
        detailFormPanel.add(rasField);
        detailFormPanel.add(new JLabel("kleur:"));
        kleurField = new JTextField();
        kleurField.setPreferredSize(new Dimension(400, 30));
        kleurField.setEnabled(false);
        detailFormPanel.add(kleurField);
        detailFormPanel.add(new JLabel("deelnemer:"));
        deelnemerField = new JTextField();
        deelnemerField.setPreferredSize(new Dimension(400, 30));
        deelnemerField.setEnabled(false);
        detailFormPanel.add(deelnemerField);
        detailFormPanel.add(new JLabel("prijs:"));
        prijsField = new JTextField();
        prijsField.setPreferredSize(new Dimension(200, 30));
        addDocumentListener(prijsField);
        detailFormPanel.add(prijsField);
        detailFormPanel.add(new JLabel("opmerking:"));
        opmerkingField = new JTextField();
        opmerkingField.setPreferredSize(new Dimension(400, 30));
        addDocumentListener(opmerkingField);
        detailFormPanel.add(opmerkingField);

        detailPanel.add(detailFormPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new MigLayout("", "[][][grow][][][]"));
        saveButton = createSaveButton();
        buttonPanel.add(saveButton);
        cancelButton = createCancelButton();
        buttonPanel.add(cancelButton);
        buttonPanel.add(new JLabel(""));
        closeButton = createCloseButton(mainWindow);
        buttonPanel.add(closeButton, "east");
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
        hoknummerField = null;
        aantalField = null;
        rasField = null;
        kleurField = null;
        deelnemerField = null;
        prijsField = null;
        opmerkingField = null;
        saveButton = null;
        cancelButton = null;
        ControllerRegistry.getInstance().unregister(this);
        ((JFrame) getView()).dispose();
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
        hoknummerField.setText(null);
        aantalField.setText(null);
        rasField.setText(null);
        kleurField.setText(null);
        deelnemerField.setText(null);
        prijsField.setText(null);
        opmerkingField.setText(null);
        setDirty(false);
    }

    @Override
    protected void loadDetail() {
        idField.setText("" + selected.getId());
        hoknummerField.setText(selected.getHoknummer().toString());
        aantalField.setText(selected.getInschrijvingLijn().getAantal().getNaam());
        rasField.setText(selected.getInschrijvingLijn().getRas().getNaam());
        kleurField.setText(selected.getInschrijvingLijn().getKleur().getNaam());
        deelnemerField.setText(selected.getInschrijvingLijn().getInschrijving().getDeelnemer().getNaam());
        prijsField.setText(selected.getPrijs());
        opmerkingField.setText(selected.getOpmerking());
        setDirty(false);
    }

    private boolean isValid() {
        return true;
    }

    private void persistChanges() {
        if (isValid()) {
            selected.setId(Long.parseLong(idField.getText()));
            selected.setPrijs(prijsField.getText());
            selected.setOpmerking(opmerkingField.getText());

            Session session = getPersistenceContext();
            HokDao hokDao = new HokDao(session);
            Transaction transaction = session.getTransaction();
            try {
                transaction.begin();
                hokDao.saveOrUpdate(selected);
                hokDao.flush();
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
                throw new RuntimeException(e);
            } finally {
                setDirty(false);
                previousId = selected.getId();
                refreshItemList(session);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void refreshItemList() {
        refreshItemList(getPersistenceContext());
    }

    public void refreshItemList(Session currentSession) {
        HokDao hokDao = new HokDao(currentSession);
        List<Hok> hokken = hokDao.findByEventId(getSelectedEvent().getId());
        itemTableModel = new EntityTableModel<>(Hok.class, hokken);
        itemTableModel.addColumn("Hoknummer", "hoknummer", 60, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Aantal", "inschrijvingLijn.aantal", 60, CellRenderers.StringCentered.getRenderer());
        itemTableModel.addColumn("Ras", "inschrijvingLijn.ras.naam", 150);
        itemTableModel.addColumn("Kleur", "inschrijvingLijn.kleur.naam", 130);
        itemTableModel.addColumn("Deelnemer", "inschrijvingLijn.inschrijving.deelnemer.naam", 150);
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    Hok h = (Hok) itemTableModel.getRow(i);
                    if (h.getId() == previousId) {
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
                parent.setVisible(false);
                parent.dispose();
                dispose();
            }
        });
        return closeButton;
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
                selected = (Hok) itemTableModel.getRow(selectedRow);
                loadDetail();
            }
        });
        return button;
    }

    private Event getSelectedEvent() {
        if (selectedEvent == null) {
            EventDao eventDao = new EventDao(getPersistenceContext());
            selectedEvent = eventDao.findSelected();
        }
        return selectedEvent;
    }

}
