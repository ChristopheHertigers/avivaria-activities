package be.avivaria.activities.gui;

import be.avivaria.activities.dao.EventRepository;
import be.avivaria.activities.dao.HokRepository;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.indigosolutions.framework.AbstractTableController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.EntityTableModel;
import be.indigosolutions.framework.cellrenderer.CellRenderers;
import net.miginfocom.swing.MigLayout;
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
public class PrijsLabelController extends AbstractTableController<Hok> {
    private static final Logger logger = LoggerFactory.getLogger(PrijsLabelController.class);

    private final HokRepository hokRepository;
    private final EventRepository eventRepository;

    // View
    private final JTextField idField;
    private final JTextField hoknummerField;
    private final JTextField aantalField;
    private final JTextField rasField;
    private final JTextField kleurField;
    private final JTextField deelnemerField;
    private final JTextField prijsField;
    private final JTextField opmerkingField;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;

    @Autowired
    public PrijsLabelController(HokRepository hokRepository, EventRepository eventRepository) {
        super(new JFrame("Prijs Labels"), 570, 250);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.hokRepository = hokRepository;
        this.eventRepository = eventRepository;

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

    @SuppressWarnings("DuplicatedCode")
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

            try {
                hokRepository.save(selected);
            } catch (Exception e) {
                logger.error("Error updating hok", e);
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
        Event selectedEvent = eventRepository.findBySelectedTrue();
        List<Hok> hokken = hokRepository.findAllByEventOrderByHoknummer(selectedEvent);
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
}
