package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.*;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.*;
import be.indigosolutions.framework.AbstractTableController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.EntityTableModel;
import be.indigosolutions.framework.celleditor.AutoCompletion;
import be.indigosolutions.framework.celleditor.ComboBoxCellEditor;
import be.indigosolutions.framework.components.Dropdown;
import be.indigosolutions.framework.components.Table;
import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * User: christophe
 * Date: 06/10/13
 * Time: 09:54
 */
@SuppressWarnings("FieldCanBeLocal")
@Controller
public class InschrijvingController extends AbstractTableController<InschrijvingHeader> {
    private static final Logger logger = LoggerFactory.getLogger(InschrijvingController.class);
    private final MainController mainController;
    private final InschrijvingRepository inschrijvingRepository;
    private final InschrijvingLijnRepository inschrijvingLijnRepository;
    private final VerenigingRepository verenigingRepository;
    private final DeelnemerRepository deelnemerRepository;
    private final AantalRepository aantalRepository;
    private final RasRepository rasRepository;
    private final KleurRepository kleurRepository;
    private final HokRepository hokRepository;
    private final EventRepository eventRepository;

    // View
    private final JTextField idField;
    private final Dropdown<Deelnemer> deelnemerCombo;
    private final JTextField naamField;
    private final JTextField straatField;
    private final JTextField woonplaatsField;
    private final JTextField telefoonField;
    private final Dropdown<Vereniging> verenigingCombo;
    private final JTextField fokkerskaartNummerField;
    private final JTextField jeugdDeelnemerField;
    private final JTextField volgnummerField;
    private final JCheckBox palmaresField;
    private final JCheckBox fokkerskaartField;
    private final JCheckBox fokkerskaart2Field;
    private final JCheckBox lidgeldField;
    private final JCheckBox lidgeld2Field;
    private final JCheckBox lidAvivariaField;
    private final JCheckBox isNewMember;

    private final Table detailTable;
    private EntityTableModel<InschrijvingLijn> detailTableModel;

    private final JButton saveButton;
    private final JButton cancelButton;
    private final JButton addLineButton;
    private final JButton removeLineButton;
    private final JButton copyLineButton;
    private final JButton pasteLineButton;
    private final JButton closeButton;

    // Model
    private long previousId = -1;
    private List<Vereniging> verenigingen;
    private List<Deelnemer> deelnemers;
    private List<Aantal> aantallen;
    private List<Ras> rassen;
    private List<Kleur> kleuren;
    private final String[] leeftijden = new String[] { "jong", "oud" };
    private Kleur noColor;

    @Autowired
    public InschrijvingController(
            MainController mainController,
            InschrijvingRepository inschrijvingRepository,
            InschrijvingLijnRepository inschrijvingLijnRepository,
            VerenigingRepository verenigingRepository,
            DeelnemerRepository deelnemerRepository,
            AantalRepository aantalRepository,
            RasRepository rasRepository,
            KleurRepository kleurRepository,
            HokRepository hokRepository,
            EventRepository eventRepository
    ) {
        super(new JFrame("Inschrijvingen"), 750, 113);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.mainController = mainController;
        this.inschrijvingRepository = inschrijvingRepository;
        this.inschrijvingLijnRepository = inschrijvingLijnRepository;
        this.verenigingRepository = verenigingRepository;
        this.deelnemerRepository = deelnemerRepository;
        this.aantalRepository = aantalRepository;
        this.rasRepository = rasRepository;
        this.kleurRepository = kleurRepository;
        this.hokRepository = hokRepository;
        this.eventRepository = eventRepository;

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 10, 10, 10), BorderFactory.createTitledBorder("Inschrijving")));

        final JPanel detailFormPanel = new JPanel(new BorderLayout());
        final JPanel formPanel = new JPanel(new MigLayout("wrap 4", "[r][l]40[r][l]"));
        formPanel.add(new JLabel("id:"));
        idField = new JTextField();
        idField.setPreferredSize(new Dimension(100, 30));
        idField.setEnabled(false);
        formPanel.add(idField, "span 3");
        formPanel.add(new JLabel("deelnemer:"));
        deelnemerCombo = new Dropdown<>();
        AutoCompletion.enable(deelnemerCombo);
        deelnemerCombo.setPreferredSize(new Dimension(400, 30));
        deelnemerCombo.setEnabled(false);
        deelnemerCombo.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                setDirty(true);
                loadDeelnemer((Deelnemer) deelnemerCombo.getSelectedItem());
            }
        });
        formPanel.add(deelnemerCombo);
        formPanel.add(new JLabel("deelnemer toevoegen:"));
        isNewMember = new JCheckBox();
        naamField = new JTextField();
        isNewMember.setEnabled(false);
        isNewMember.setSelected(false);
        isNewMember.addActionListener(e -> {
            JCheckBox checkBox = (JCheckBox) e.getSource();
            loadDeelnemer(null);
            deelnemerCombo.setSelectedItem(null);
            if (checkBox.isSelected()) {
                setStatusMemberDetail(true);
                naamField.requestFocusInWindow();
            } else {
                setStatusMemberDetail(false);
            }
        });
        formPanel.add(isNewMember);
        formPanel.add(new JLabel("naam:"));
        naamField.setPreferredSize(new Dimension(400, 30));
        naamField.setEnabled(false);
        addDocumentListener(naamField);
        formPanel.add(naamField);
        formPanel.add(new JLabel("volgnummer:"));
        volgnummerField = new JTextField();
        volgnummerField.setPreferredSize(new Dimension(50, 30));
        volgnummerField.setEnabled(false);
        addDocumentListener(volgnummerField);
        formPanel.add(volgnummerField);
        formPanel.add(new JLabel("straat:"));
        straatField = new JTextField();
        straatField.setPreferredSize(new Dimension(400, 30));
        straatField.setEnabled(false);
        addDocumentListener(straatField);
        formPanel.add(straatField);
        formPanel.add(new JLabel("palmares:"));
        palmaresField = new JCheckBox();
        palmaresField.setEnabled(false);
        addChangeListener(palmaresField);
        formPanel.add(palmaresField);
        formPanel.add(new JLabel("woonplaats:"));
        woonplaatsField = new JTextField();
        woonplaatsField.setPreferredSize(new Dimension(400, 30));
        woonplaatsField.setEnabled(false);
        addDocumentListener(woonplaatsField);
        formPanel.add(woonplaatsField);
        formPanel.add(new JLabel("fokkerskaart:"));
        fokkerskaartField = new JCheckBox();
        fokkerskaartField.setEnabled(false);
        addChangeListener(fokkerskaartField);
        formPanel.add(fokkerskaartField);
        formPanel.add(new JLabel("telefoon:"));
        telefoonField = new JTextField();
        telefoonField.setPreferredSize(new Dimension(200, 30));
        telefoonField.setEnabled(false);
        addDocumentListener(telefoonField);
        formPanel.add(telefoonField);
        formPanel.add(new JLabel("fokkerskaart (2e):"));
        fokkerskaart2Field = new JCheckBox();
        fokkerskaart2Field.setEnabled(false);
        addChangeListener(fokkerskaart2Field);
        formPanel.add(fokkerskaart2Field);
        formPanel.add(new JLabel("vereniging:"));
        verenigingCombo = new Dropdown<>();
        AutoCompletion.enable(verenigingCombo);
        verenigingCombo.setPreferredSize(new Dimension(400, 30));
        verenigingCombo.setEnabled(false);
        addItemChangeListener(verenigingCombo);
        formPanel.add(verenigingCombo);
        formPanel.add(new JLabel("lidgeld:"));
        lidgeldField = new JCheckBox();
        lidgeldField.setEnabled(false);
        addChangeListener(lidgeldField);
        formPanel.add(lidgeldField);
        formPanel.add(new JLabel("fokkerskaart:"));
        fokkerskaartNummerField = new JTextField();
        fokkerskaartNummerField.setPreferredSize(new Dimension(200, 30));
        fokkerskaartNummerField.setEnabled(false);
        addDocumentListener(fokkerskaartNummerField);
        formPanel.add(fokkerskaartNummerField);
        formPanel.add(new JLabel("lidgeld (2e):"));
        lidgeld2Field = new JCheckBox();
        lidgeld2Field.setEnabled(false);
        addChangeListener(lidgeld2Field);
        formPanel.add(lidgeld2Field);
        formPanel.add(new JLabel("jeugddeelnemer:"));
        jeugdDeelnemerField = new JTextField();
        jeugdDeelnemerField.setPreferredSize(new Dimension(100, 30));
        jeugdDeelnemerField.setEnabled(false);
        addDocumentListener(jeugdDeelnemerField);
        formPanel.add(jeugdDeelnemerField);
        formPanel.add(new JLabel("lid avivaria:"));
        lidAvivariaField = new JCheckBox();
        lidAvivariaField.setEnabled(false);
        addChangeListener(lidAvivariaField);
        formPanel.add(lidAvivariaField);

        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        detailFormPanel.add(formPanel, BorderLayout.NORTH);
        detailFormPanel.add(tablePanel, BorderLayout.CENTER);

        detailTable = new Table() {
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < detailFormPanel.getWidth();
            }
        };
        itemTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        itemTable.setFillsViewportHeight(true);
        detailTable.setRowHeight(20);
        detailTable.setCellSelectionEnabled(true);
        JScrollPane detailScrollPane = new JScrollPane(detailTable);
        //detailScrollPane.setPreferredSize(new Dimension(650, 100));
        tablePanel.add(detailScrollPane, BorderLayout.CENTER);
        JPanel tableButtonPanel = new JPanel(new GridLayout(4,1,0,0));
        addLineButton = createAddLineButton();
        tableButtonPanel.add(addLineButton);
        removeLineButton = createRemoveLineButton();
        tableButtonPanel.add(removeLineButton);
        copyLineButton = createCopyLineButton();
        tableButtonPanel.add(copyLineButton);
        pasteLineButton = createPasteLineButton();
        tableButtonPanel.add(pasteLineButton);
        tablePanel.add(tableButtonPanel, BorderLayout.EAST);

        detailFormPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                detailScrollPane.setPreferredSize(new Dimension(detailFormPanel.getWidth()-100, detailFormPanel.getHeight()-345));
            }
        });


        detailTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        detailTable.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                detailTable.editCellAt(detailTable.getSelectedRow(), detailTable.getSelectedColumn());
            }
        });

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
        mainWindow.setMinimumSize(new Dimension(800, 700));
        mainWindow.setPreferredSize(new Dimension(800, 700));
        mainWindow.setLocation(350, 0);
    }

    @Override
    public void show() {
        refreshRelations();
        refreshItemList();
        refreshDetailList();
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    @SuppressWarnings("unchecked")
    private void refreshRelations() {
        verenigingen = verenigingRepository.findAllByOrderByNaam();
        deelnemers = deelnemerRepository.findAllByOrderByNaam();
        aantallen = aantalRepository.findAllByOrderByNaam();
        rassen = rasRepository.findAllByOrderByNaam();
        kleuren = kleurRepository.findAllByOrderByNaam();
        for (Kleur kleur : kleuren) {
            if (kleur.getId() == 0L) {
                noColor = kleur;
                break;
            }
        }
        deelnemerCombo.setModel(new DefaultComboBoxModel<>(deelnemers.toArray(new Deelnemer[0])));
        verenigingCombo.setModel(new DefaultComboBoxModel<>(verenigingen.toArray(new Vereniging[0])));
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
        isNewMember.setEnabled(dirty);
    }

    @Override
    protected void clearDetail() {
        idField.setText(null);
        deelnemerCombo.setSelectedItem(null);
        deelnemerCombo.setEnabled(false);
        naamField.setText(null);
        naamField.setEnabled(false);
        straatField.setText(null);
        straatField.setEnabled(false);
        woonplaatsField.setText(null);
        woonplaatsField.setEnabled(false);
        telefoonField.setText(null);
        telefoonField.setEnabled(false);
        verenigingCombo.setSelectedItem(null);
        verenigingCombo.setEnabled(false);
        fokkerskaartNummerField.setText(null);
        fokkerskaartNummerField.setEnabled(false);
        jeugdDeelnemerField.setText(null);
        jeugdDeelnemerField.setEnabled(false);
        volgnummerField.setText(null);
        volgnummerField.setEnabled(false);
        palmaresField.setSelected(false);
        palmaresField.setEnabled(false);
        fokkerskaartField.setSelected(false);
        fokkerskaartField.setEnabled(false);
        fokkerskaart2Field.setSelected(false);
        fokkerskaart2Field.setEnabled(false);
        lidgeldField.setSelected(false);
        lidgeldField.setEnabled(false);
        lidgeld2Field.setSelected(false);
        lidgeld2Field.setEnabled(false);
        lidAvivariaField.setSelected(false);
        lidAvivariaField.setEnabled(false);
        isNewMember.setSelected(false);

        setDirty(false);
    }

    private void loadDeelnemer(Deelnemer deelnemer) {
        if (deelnemer != null) {
            checkFokkerskaartNummer(deelnemer);
            naamField.setText(deelnemer.getNaam());
            straatField.setText(deelnemer.getStraat());
            woonplaatsField.setText(deelnemer.getWoonplaats());
            telefoonField.setText(deelnemer.getTelefoon());
            verenigingCombo.setSelectedItem(deelnemer.getVereniging());
            fokkerskaartNummerField.setText(deelnemer.getFokkerskaartNummer());
            jeugdDeelnemerField.setText("" + (deelnemer.getJeugddeelnemer() == null ? "" : deelnemer.getJeugddeelnemer()));
            lidAvivariaField.setSelected(deelnemer.getVereniging() != null && deelnemer.getVereniging().getId() == 1);
            isNewMember.setSelected(false);
        } else {
            naamField.setText(null);
            straatField.setText(null);
            woonplaatsField.setText(null);
            telefoonField.setText(null);
            verenigingCombo.setSelectedItem(null);
            fokkerskaartNummerField.setText(null);
            jeugdDeelnemerField.setText(null);
        }
    }

    private void checkFokkerskaartNummer(Deelnemer deelnemer) {
        String oudFokkerskaartNr = deelnemer.getFokkerskaartNummer();
        if (StringUtils.isNotEmpty(oudFokkerskaartNr)) {
            String noSpaces = oudFokkerskaartNr.replaceAll("\\s", "");

            // when not conforming to format, abort update
            if (!StringUtils.isNumeric(noSpaces) || noSpaces.length() != 10) return;

            // create valid number for the current year
            String nieuwFokkerskaartNr = (getCurrentYear()-2000) + " " + noSpaces.charAt(2) + " " + noSpaces.substring(3,6) + " " + noSpaces.substring(6);
            if (!noSpaces.equals(nieuwFokkerskaartNr)) {
                // only update when the numbers differ
                try {
                    deelnemerRepository.findById(deelnemer.getId()).ifPresent(d -> {
                        deelnemer.setFokkerskaartNummer(nieuwFokkerskaartNr);
                        deelnemerRepository.save(deelnemer);
                    });
                } catch (Exception e) {
                    logger.error("Failed te update deelnemer", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.YEAR);
    }

    @Override
    protected void loadDetail() {
        refreshRelations();
        Deelnemer deelnemer = selected.getDeelnemer();
        idField.setText("" + selected.getId());
        deelnemerCombo.setSelectedItem(deelnemer);
        deelnemerCombo.setEnabled(true);
        loadDeelnemer(deelnemer);
        volgnummerField.setText("" + selected.getVolgnummer());
        palmaresField.setSelected(bool(selected.getPalmares(), false));
        palmaresField.setEnabled(true);
        fokkerskaartField.setSelected(bool(selected.getFokkerskaart(),false));
        fokkerskaartField.setEnabled(true);
        fokkerskaart2Field.setSelected(bool(selected.getFokkerskaart2(),false));
        fokkerskaart2Field.setEnabled(true);
        lidgeldField.setSelected(bool(selected.getLidgeld(), false));
        lidgeldField.setEnabled(true);
        lidgeld2Field.setSelected(bool(selected.getLidgeld2(), false));
        lidgeld2Field.setEnabled(true);
        lidAvivariaField.setSelected(bool(selected.getLidAvivaria(), false));
        lidAvivariaField.setEnabled(true);
        refreshDetailList();

        setDirty(false);
    }

    private void setStatusMemberDetail(boolean enabled) {
        naamField.setEnabled(enabled);
        straatField.setEnabled(enabled);
        woonplaatsField.setEnabled(enabled);
        telefoonField.setEnabled(enabled);
        verenigingCombo.setEnabled(enabled);
        fokkerskaartNummerField.setEnabled(enabled);
        deelnemerCombo.setEnabled(!enabled);
    }

    @SuppressWarnings("SameParameterValue")
    private boolean bool(Boolean bool, boolean def) {
        if (bool == null) return def;
        return bool;
    }

    private boolean isValid() {
        if (!StringUtils.isNumeric(volgnummerField.getText())) return false;
        if (StringUtils.isNotBlank(jeugdDeelnemerField.getText()) && !StringUtils.isNumeric(jeugdDeelnemerField.getText())) return false;
        for (int i = 0; i < detailTableModel.getRowCount(); i++) {
            InschrijvingLijn lijn = detailTableModel.getRow(i);
            if (lijn.getAantal() == null) return false;
            if (lijn.getLeeftijd() == null) return false;
            if (lijn.getRas() == null) return false;
        }
        return true;
    }

    private void persistChanges() {
        if (isValid()) {
            try {
                boolean existingInschrijving = selected != null && selected.getId() != null;
                List<InschrijvingLijn> originalLines = existingInschrijving ? inschrijvingLijnRepository.findAllByInschrijvingOrderById(selected) : Collections.emptyList();

                if (existingInschrijving) {
                    Optional<InschrijvingHeader> existing = inschrijvingRepository.findById(Long.parseLong(idField.getText()));
                    existing.ifPresent(inschrijvingHeader -> selected = inschrijvingHeader);
                }

                selected.setVolgnummer(Long.parseLong(volgnummerField.getText()));
                selected.setEvent(getSelectedEvent());
                selected.setPalmares(palmaresField.isSelected());
                selected.setFokkerskaart(fokkerskaartField.isSelected());
                selected.setFokkerskaart2(fokkerskaart2Field.isSelected());
                selected.setLidgeld(lidgeldField.isSelected());
                selected.setLidgeld2(lidgeld2Field.isSelected());
                selected.setLidAvivaria(lidAvivariaField.isSelected());

                if (isNewMember.isSelected()) {
                    Deelnemer deelnemer = new Deelnemer();
                    deelnemer.setNaam(naamField.getText());
                    deelnemer.setStraat(straatField.getText());
                    deelnemer.setWoonplaats(woonplaatsField.getText());
                    deelnemer.setTelefoon(telefoonField.getText());
                    deelnemer.setVereniging((Vereniging) verenigingCombo.getSelectedItem());
                    deelnemer.setFokkerskaartNummer(fokkerskaartField.getText());
                    deelnemer.setJeugddeelnemer(StringUtils.isBlank(jeugdDeelnemerField.getText()) ? null : Integer.parseInt(jeugdDeelnemerField.getText()));

                    deelnemer.setId(null);
                    deelnemerRepository.save(deelnemer);

                    selected.setDeelnemer(deelnemer);
                    // update combo boxes
                    SwingUtilities.invokeLater(this::refreshRelations);

                } else {
                    selected.setDeelnemer((Deelnemer) deelnemerCombo.getSelectedItem());
                }

                selected = inschrijvingRepository.save(selected);
                idField.setText(""+selected.getId());
                for (int i = 0; i < detailTableModel.getRowCount(); i++) {
                    InschrijvingLijn lijn = detailTableModel.getRow(i);
                    if (lijn.getKleur() == null) {
                        lijn.setKleur(noColor);
                    }
                    inschrijvingLijnRepository.save(lijn);
                }
                // check for lijnen to delete
                List<InschrijvingLijn> toDelete = new ArrayList<>();
                for (InschrijvingLijn original : originalLines) {
                    boolean found = false;
                    for (int i = 0; i < detailTableModel.getRowCount(); i++) {
                        InschrijvingLijn lijn = detailTableModel.getRow(i);
                        if (lijn.getId().equals(original.getId())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) toDelete.add(original);
                }
                inschrijvingLijnRepository.deleteAll(toDelete);
            } catch (Exception e) {
                logger.error("Failed to save inschrijving", e);
                throw new RuntimeException(e);
            } finally {
                setDirty(false);
                previousId = selected.getId() == null ? -1 : selected.getId();
                refreshItemList();

                SwingUtilities.invokeLater(mainController::loadData);
            }
        }
    }

    @Override
    public void refreshItemList() {
        List<InschrijvingHeader> inschrijvingen = inschrijvingRepository.findAllByEventOrderByDeelnemer_Naam(getSelectedEvent());
        itemTableModel = new EntityTableModel<>(InschrijvingHeader.class, inschrijvingen);
        itemTableModel.addColumn("Naam", "deelnemer.naam", 600);
        itemTableModel.addColumn("Volgnummer", "volgnummer", 150);
        itemTable.setModel(itemTableModel);
        setColumnProperties(itemTable, itemTableModel);
        SwingUtilities.invokeLater(() -> {
            if (previousId >= 0) {
                for (int i = 0; i < itemTableModel.getRowCount(); i++) {
                    InschrijvingHeader s = (InschrijvingHeader) itemTableModel.getRow(i);
                    if (s.getId() == previousId) {
                        itemTable.setRowSelectionInterval(i,i);
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

    public void refreshDetailList() {
        List<InschrijvingLijn> inschrijvingLijnen;
        if (selected != null && selected.getId() != null) {
            inschrijvingLijnen = inschrijvingLijnRepository.findAllByInschrijvingOrderById(selected);
        } else {
            inschrijvingLijnen = new ArrayList<>();
        }
        Dropdown<Aantal> aantalCombo = new Dropdown<>(aantallen);
        AutoCompletion.enable(aantalCombo);
        Dropdown<String> leeftijdCombo = new Dropdown<>(leeftijden);
        AutoCompletion.enable(leeftijdCombo);
        Dropdown<Ras> rasCombo = new Dropdown<>(rassen);
        AutoCompletion.enable(rasCombo);
        Dropdown<Kleur> kleurCombo = new Dropdown<>(kleuren);
        AutoCompletion.enable(kleurCombo);

        detailTableModel = new EntityTableModel<>(InschrijvingLijn.class, inschrijvingLijnen);
        detailTableModel.addColumn("Aantal", "aantal", 50, new ComboBoxCellEditor(aantalCombo), true);
        detailTableModel.addColumn("Leeftijd", "leeftijd", 50, new ComboBoxCellEditor(leeftijdCombo), true);
        detailTableModel.addColumn("Ras", "ras", 300, new ComboBoxCellEditor(rasCombo), true);
        detailTableModel.addColumn("Kleur", "kleur", 150, new ComboBoxCellEditor(kleurCombo), true);
        detailTableModel.addColumn("Ring nr", "ringnummer", 70, null, true);
        detailTableModel.addColumn("Prijs", "prijs", 50, null, true);
        detailTableModel.addTableModelListener(e -> setDirty(true));
        detailTable.setModel(detailTableModel);
        setColumnProperties(detailTable, detailTableModel);
        SwingUtilities.invokeLater(() -> {
            if (detailTable.getRowCount() > 0) {
                detailTable.setRowSelectionInterval(0, 0);
                detailTable.scrollRectToVisible(new Rectangle(detailTable.getCellRect(0, 0, true)));
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
                    inschrijvingRepository.findById(selected.getId()).ifPresent(i -> selected = i);
                    if (hokRepository.countByEvent(selected.getEvent()) > 0) {
                        JOptionPane.showMessageDialog(parent, "De hoknummers moeten eerst verwijderd worden \n(via menu Activiteit > Hoknummers > Verwijderen).");
                        return;
                    }
                    try {
                        List<InschrijvingLijn> lijnen = inschrijvingLijnRepository.findAllByInschrijvingOrderById(selected);
                        inschrijvingLijnRepository.deleteAll(lijnen);
                        inschrijvingRepository.delete(selected);
                    } catch(Exception ex) {
                        logger.error("Failed to delete inschrijving", ex);
                        throw new RuntimeException(ex);
                    } finally {
                        setDirty(false);
                        refreshItemList();

                        SwingUtilities.invokeLater(mainController::loadData);
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
                Long volgnummer = inschrijvingRepository.nextVolgnummerByEvent(getSelectedEvent());
                selected = new InschrijvingHeader();
                selected.setVolgnummer(volgnummer);
                loadDetail();
                setDirty(true);
                SwingUtilities.invokeLater(deelnemerCombo::requestFocus);
            }
        });
        return button;
    }

    private ImageIcon loadIcon(String name) {
        try {
            return new ImageIcon(ImageIO.read(getClass().getResource(name)));
        } catch (IOException ex) {
            //intentionally left blank
        }
        return null;
    }

    private JButton createAddLineButton() {
        JButton button = new JButton();
        button.setMargin(new Insets(2, 3, 2, 3));
        button.setIcon(loadIcon("add.png"));
        registerAction(button, new DefaultAction("addLine") {
            public void actionPerformed(ActionEvent e) {
                InschrijvingLijn lijn = new InschrijvingLijn();
                lijn.setId(null);
                lijn.setInschrijving(selected);
                detailTableModel.addRow(lijn);
                setDirty(true);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        detailTable.changeSelection(detailTableModel.getRowCount()-1, 0, false , false);
                        detailTable.scrollRectToVisible(new Rectangle(detailTable.getCellRect(detailTableModel.getRowCount()-1, 0, true)));
                        detailTable.editCellAt(detailTableModel.getRowCount()-1, 0);
                    }
                });
            }
        });
        return button;
    }

    private JButton createRemoveLineButton() {
        JButton button = new JButton();
        button.setMargin(new Insets(2, 3, 2, 3));
        button.setIcon(loadIcon("remove.png"));
        registerAction(button, new DefaultAction("removeLine") {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = detailTable.getSelectedRow();
                if (detailTable.getCellEditor() != null) {
                    detailTable.getCellEditor().stopCellEditing();
                }
                if (selectedRow >= 0) {
                    detailTableModel.removeRow(selectedRow);
                }
                setDirty(true);
            }
        });
        return button;
    }

    private InschrijvingLijn copy = null;
    private JButton createCopyLineButton() {
        JButton button = new JButton();
        button.setMargin(new Insets(2, 3, 2, 3));
        button.setIcon(loadIcon("copy.png"));
        registerAction(button, new DefaultAction("copyLine") {
            public void actionPerformed(ActionEvent e) {
                copy = detailTableModel.getRow(detailTable.getSelectedRow());
            }
        });
        return button;
    }

    private JButton createPasteLineButton() {
        JButton button = new JButton();
        button.setMargin(new Insets(2, 3, 2, 3));
        button.setIcon(loadIcon("paste.png"));
        registerAction(button, new DefaultAction("pasteLine") {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (copy != null) {
                        if (detailTable.getCellEditor() != null) {
                            detailTable.getCellEditor().stopCellEditing();
                        }
                        InschrijvingLijn clone = (InschrijvingLijn) copy.clone();
                        clone.setId(null);
                        detailTableModel.addRow(clone);
                        setDirty(true);
                        SwingUtilities.invokeLater(() -> {
                            detailTable.changeSelection(detailTableModel.getRowCount() - 1, 0, false, false);
                            detailTable.scrollRectToVisible(new Rectangle(detailTable.getCellRect(detailTableModel.getRowCount() - 1, 0, true)));
                            detailTable.editCellAt(detailTable.getSelectedRow(), detailTable.getSelectedColumn());
                        });
                    }
                } catch (CloneNotSupportedException e1) {
                    throw new RuntimeException(e1);
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
                if (itemTable.getRowCount() > 0) {
                    int selectedRow = itemTable.getSelectedRow() == -1 ? 0 : itemTable.getSelectedRow();
                    selected = (InschrijvingHeader) itemTableModel.getRow(selectedRow);
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
