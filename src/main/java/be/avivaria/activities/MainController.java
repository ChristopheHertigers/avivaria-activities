package be.avivaria.activities;

import be.avivaria.activities.dao.EventRepository;
import be.avivaria.activities.dao.HokRepository;
import be.avivaria.activities.dao.InschrijvingRepository;
import be.avivaria.activities.gui.ActivityMenuItem;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.HokType;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.DefaultAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

@Controller
public class MainController extends AbstractController {

    @SuppressWarnings("unused")
    Logger logger = LoggerFactory.getLogger(MainController.class);

    private final EventRepository eventRepository;
    private final InschrijvingRepository inschrijvingRepository;
    private final HokRepository hokRepository;

    private final JPanel inschrijvingenPanel;
    private final JPanel hoknummerTitlePanel;
    private final JPanel hoknummerPanel;
    private final JPanel palmaresTitlePanel;
    private final JPanel palmaresPanel;
    private final JPanel labelsTitlePanel;
    private final JPanel labelsPanel;

    private final JLabel huidigeActiviteitLabel = new JLabel();
    private final JLabel aantalInschrijvingen = new JLabel();
    private final JLabel aantalHokkenType1 = new JLabel();
    private final JLabel aantalHokkenType2 = new JLabel();
    private final JLabel aantalHokkenType3 = new JLabel();
    private final JLabel aantalHokkenType4 = new JLabel();
    private final JLabel aantalHokkenType5 = new JLabel();
    private final JLabel aantalHokken = new JLabel();

    private final JButton exitButton;

    private Event selectedEvent;

    @Autowired
    public MainController(
            EventRepository eventRepository,
            InschrijvingRepository inschrijvingRepository,
            HokRepository hokRepository
    ) {

        super(new JFrame("Avivaria Activiteiten Beheer"));

        this.eventRepository = eventRepository;
        this.inschrijvingRepository = inschrijvingRepository;
        this.hokRepository = hokRepository;

        final JFrame mainWindow = (JFrame) getView();

        // Decoration etc.
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(null);

        // Menu
        JMenuBar menuBar = ActivityMenuItem.createMenu(this);
        mainWindow.setJMenuBar(menuBar);

        // Current activity panel
        mainWindow.add(createTitle("Huidige Activiteit", new Rectangle(10, 10, 820, 25), null));
        mainWindow.add(createActitivyPanel());

        // Inschrijvingen panel
        mainWindow.add(createTitle("Inschrijvingen", new Rectangle(10, 80, 820, 30), new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!inschrijvingenPanel.isVisible()) {
                    inschrijvingenPanel.setVisible(true);
                    hoknummerTitlePanel.setBounds(10, 390, 820, 30);
                    hoknummerPanel.setVisible(false);
                    palmaresTitlePanel.setBounds(10, 430, 820, 30);
                    palmaresPanel.setVisible(false);
                    labelsTitlePanel.setBounds(10, 470, 820, 30);
                    labelsPanel.setVisible(false);
                    mainWindow.repaint();
                }
            }
        }));
        inschrijvingenPanel = createInschrijvingPanel();
        mainWindow.add(inschrijvingenPanel);

        // Hoknummers panel
        hoknummerTitlePanel = createTitle("Hoknummers", new Rectangle(10, 390, 820, 30), new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!hoknummerPanel.isVisible()) {
                    inschrijvingenPanel.setVisible(false);
                    hoknummerTitlePanel.setBounds(10, 120, 820, 30);
                    hoknummerPanel.setVisible(true);
                    palmaresTitlePanel.setBounds(10, 430, 820, 30);
                    palmaresPanel.setVisible(false);
                    labelsTitlePanel.setBounds(10, 470, 820, 30);
                    labelsPanel.setVisible(false);
                    mainWindow.repaint();
                }
            }
        });
        mainWindow.add(hoknummerTitlePanel);
        hoknummerPanel = createHoknummerPanel();
        hoknummerPanel.setVisible(false);
        mainWindow.add(hoknummerPanel);

        // Palmares panel
        palmaresTitlePanel = createTitle("Palmares", new Rectangle(10, 430, 820, 30), new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!palmaresPanel.isVisible()) {
                    inschrijvingenPanel.setVisible(false);
                    hoknummerTitlePanel.setBounds(10, 120, 820, 30);
                    hoknummerPanel.setVisible(false);
                    palmaresTitlePanel.setBounds(10, 160, 820, 30);
                    palmaresPanel.setVisible(true);
                    labelsTitlePanel.setBounds(10, 470, 820, 30);
                    labelsPanel.setVisible(false);
                    mainWindow.repaint();
                }
            }
        });
        mainWindow.add(palmaresTitlePanel);
        palmaresPanel = createPalmaresPanel();
        palmaresPanel.setVisible(false);
        mainWindow.add(palmaresPanel);

        // Label panel
        labelsTitlePanel = createTitle("Labels", new Rectangle(10, 470, 820, 30), new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!hoknummerPanel.isVisible()) {
                    inschrijvingenPanel.setVisible(false);
                    hoknummerTitlePanel.setBounds(10, 120, 820, 30);
                    hoknummerPanel.setVisible(false);
                    palmaresTitlePanel.setBounds(10, 160, 820, 30);
                    palmaresPanel.setVisible(false);
                    labelsTitlePanel.setBounds(10, 200, 820, 30);
                    labelsPanel.setVisible(true);
                    mainWindow.repaint();
                }
            }
        });
        mainWindow.add(labelsTitlePanel);
        labelsPanel = createLabelsPanel();
        labelsPanel.setVisible(false);
        mainWindow.add(labelsPanel);

        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBounds(10, 520, 825, 30);
        exitButton = createExitButton();
        bottomPanel.add(exitButton, BorderLayout.EAST);
        mainWindow.add(bottomPanel);

        // Display the window
        mainWindow.setMinimumSize(new Dimension(860, 620));
        mainWindow.setPreferredSize(new Dimension(860, 620));
        mainWindow.setLocation(300, 0);
    }

    @Override
    public void show() {
        // load data
        loadData();
        super.show();
        exitButton.requestFocus();
    }

    private JPanel createTitle(String title, Rectangle r, MouseAdapter adapter) {
        final JPanel p = new JPanel(new BorderLayout());
        p.setBounds(r.x, r.y, r.width, r.height);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 16));
        p.add(titleLabel, BorderLayout.WEST);
        if (adapter != null) {
            p.addMouseListener(adapter);
        }
        return p;
    }

    private JPanel createActitivyPanel() {
        JPanel panel = new JPanel();
        panel.setBounds(10, 35, 820, 30);
        huidigeActiviteitLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
        huidigeActiviteitLabel.setForeground(new Color(0, 0, 128));
        panel.add(huidigeActiviteitLabel);
        return panel;
    }

    private JButton createExitButton() {
        JButton exitButton = new JButton("Sluit");
        registerAction(exitButton, new DefaultAction("exit") {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        return exitButton;
    }

    private JButton createInschrijvingDetailButton() {
        JButton inschrijvingDetailButton = new JButton("Ga naar inschrijving");
        inschrijvingDetailButton.setBounds(670, 3, 150, 28);
        registerAction(inschrijvingDetailButton, ActivityMenuItem.Inschrijvingen.getAction(this));
        return inschrijvingDetailButton;
    }

    private JPanel createInschrijvingPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 110, 820, 30);

        JLabel aantalInschrijvingenLabel = new JLabel("Aantal inschrijvingen:");
        aantalInschrijvingenLabel.setBounds(30, 0, 150, 30);
        panel.add(aantalInschrijvingenLabel);

        aantalInschrijvingen.setBounds(180, 0, 50, 30);
        panel.add(aantalInschrijvingen);
        panel.add(createInschrijvingDetailButton());

        return panel;
    }

    private JPanel createHoknummerPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 150, 820, 60);

        JLabel aantalHokkenLabel = new JLabel("Aantal hokken:");
        aantalHokkenLabel.setBounds(30, 30, 150, 30);
        panel.add(aantalHokkenLabel);

        createHokTypeLabels(panel, aantalHokkenType1, "Type 1", "0", 180);
        createHokTypeLabels(panel, aantalHokkenType2, "Type 2", "0", 235);
        createHokTypeLabels(panel, aantalHokkenType3, "Type 3", "0", 290);
        createHokTypeLabels(panel, aantalHokkenType4, "Type 4", "0", 345);
        createHokTypeLabels(panel, aantalHokkenType5, "Type 5", "0", 400);
        createHokTypeLabels(panel, aantalHokken, "Totaal", "0", 580);

        panel.add(createHoknummersButton());
        panel.add(createAfdrukkenInschrijversButton());

        return panel;
    }

    private JButton createHoknummersButton() {
        JButton hoknummersButton = new JButton("Hoknummers");
        hoknummersButton.setBounds(655, 3, 165, 28);
        registerAction(hoknummersButton, ActivityMenuItem.HoknummersToekennen.getAction(this));
        return hoknummersButton;
    }

    private JButton createAfdrukkenInschrijversButton() {
        JButton afdrukkenInschrijversButton = new JButton("Afdrukken inschrijvers");
        afdrukkenInschrijversButton.setBounds(655, 33, 165, 28);
        registerAction(afdrukkenInschrijversButton, ActivityMenuItem.InschrijvingenAfdrukken.getAction(this));
        return afdrukkenInschrijversButton;
    }

    @SuppressWarnings("SameParameterValue")
    private void createHokTypeLabels(JPanel panel, JLabel aantalHokkenType, String title, String value, int x) {
        JLabel aantalHokkenType1Label = new JLabel(title);
        aantalHokkenType1Label.setBounds(x, 10, 50, 20);
        aantalHokkenType1Label.setFont(aantalHokkenType1Label.getFont().deriveFont(Font.BOLD));
        aantalHokkenType1Label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(aantalHokkenType1Label);

        aantalHokkenType.setText(value);
        aantalHokkenType.setBounds(x, 30, 50, 30);
        aantalHokkenType.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(aantalHokkenType);
    }

    private JPanel createPalmaresPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 190, 820, 97);

        JButton predicatenButton = new JButton("Predicaten");
        predicatenButton.setBounds(230, 3, 90, 28);
        registerAction(predicatenButton, ActivityMenuItem.PredicatenToekennen.getAction(this));
        panel.add(predicatenButton);

        JButton afdrukkenVerenigingenButton = new JButton("Afdrukken verenigingen");
        afdrukkenVerenigingenButton.setBounds(325, 3, 165, 28);
        registerAction(afdrukkenVerenigingenButton, ActivityMenuItem.VerenigingenAfdrukken.getAction(this));
        panel.add(afdrukkenVerenigingenButton);

        JButton afdrukkenDeelnemersButton = new JButton("Afdrukken deelnemers");
        afdrukkenDeelnemersButton.setBounds(495, 3, 160, 28);
        registerAction(afdrukkenDeelnemersButton, ActivityMenuItem.DeelnemersAfdrukken.getAction(this));
        panel.add(afdrukkenDeelnemersButton);

        JButton afdrukkenJeugdButton = new JButton("Afdrukken jeugd");
        afdrukkenJeugdButton.setBounds(660, 3, 160, 28);
        registerAction(afdrukkenJeugdButton, ActivityMenuItem.JeugdDeelnemersAfdrukken.getAction(this));
        panel.add(afdrukkenJeugdButton);

        JButton keurmeesterButton = new JButton("Keurmeester");
        keurmeesterButton.setBounds(325, 35, 165, 28);
        registerAction(keurmeesterButton, ActivityMenuItem.Keurmeesters.getAction(this));
        panel.add(keurmeesterButton);

        JButton afdrukkenPalmaresButton = new JButton("Afdrukken palmares");
        afdrukkenPalmaresButton.setBounds(495, 35, 160, 28);
        registerAction(afdrukkenPalmaresButton, ActivityMenuItem.PalmaresAfdrukken.getAction(this));
        panel.add(afdrukkenPalmaresButton);

        JButton afdrukkenKampioenenButton = new JButton("Afdrukken kampioenen");
        afdrukkenKampioenenButton.setBounds(660, 35, 160, 28);
        registerAction(afdrukkenKampioenenButton, ActivityMenuItem.KampioenenAfdrukken.getAction(this));
        panel.add(afdrukkenKampioenenButton);

        JButton afdrukkenAantalDierenLedenButton = new JButton("Afdrukken dieren lid");
        afdrukkenAantalDierenLedenButton.setBounds(495, 67, 160, 28);
        registerAction(afdrukkenAantalDierenLedenButton, ActivityMenuItem.AantalDierenLedenAfdrukken.getAction(this));
        panel.add(afdrukkenAantalDierenLedenButton);

        JButton afdrukkenAantalDierenNietLedenButton = new JButton("Afdrukken dieren rest");
        afdrukkenAantalDierenNietLedenButton.setBounds(660, 67, 160, 28);
        registerAction(afdrukkenAantalDierenNietLedenButton, ActivityMenuItem.AantalDierenNietLedenAfdrukken.getAction(this));
        panel.add(afdrukkenAantalDierenNietLedenButton);

        return panel;
    }

    private JPanel createLabelsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 230, 820, 30);

        JButton afdrukkenHokLabelsButton = new JButton("Afdrukken hoklabels");
        afdrukkenHokLabelsButton.setBounds(330, 3, 160, 28);
        registerAction(afdrukkenHokLabelsButton, ActivityMenuItem.HokLabelsAfdrukken.getAction(this));
        panel.add(afdrukkenHokLabelsButton);

        JButton afdrukkenPrijsLabelsButton = new JButton("Afdrukken prijslabels");
        afdrukkenPrijsLabelsButton.setBounds(495, 3, 160, 28);
        registerAction(afdrukkenPrijsLabelsButton, ActivityMenuItem.PrijsLabelsAfdrukken.getAction(this));
        panel.add(afdrukkenPrijsLabelsButton);

        JButton editPrijsLabelsButton = new JButton("Wijzig prijslabels");
        editPrijsLabelsButton.setBounds(660, 3, 160, 28);
        registerAction(editPrijsLabelsButton, ActivityMenuItem.EditPrijsLabels.getAction(this));
        panel.add(editPrijsLabelsButton);

        return panel;
    }


    public void loadData() {
        selectedEvent = eventRepository.findBySelectedTrue();
        long nbrInschrijvingen = inschrijvingRepository.countByEvent(selectedEvent);

        huidigeActiviteitLabel.setText(selectedEvent.getNaam());
        aantalInschrijvingen.setText("" + nbrInschrijvingen);

        Map<HokType,Long> countsPerHokType = hokRepository.countPerHokTypeByEvent(selectedEvent);
        long nbrHokken = hokRepository.countByEvent(selectedEvent);

        aantalHokkenType1.setText(countsPerHokType.get(HokType.Type1) == null ? "0" : ""+countsPerHokType.get(HokType.Type1));
        aantalHokkenType2.setText(countsPerHokType.get(HokType.Type2) == null ? "0" : ""+countsPerHokType.get(HokType.Type2));
        aantalHokkenType3.setText(countsPerHokType.get(HokType.Type3) == null ? "0" : ""+countsPerHokType.get(HokType.Type3));
        aantalHokkenType4.setText(countsPerHokType.get(HokType.Type4) == null ? "0" : ""+countsPerHokType.get(HokType.Type4));
        aantalHokkenType5.setText(countsPerHokType.get(HokType.Type5) == null ? "0" : ""+countsPerHokType.get(HokType.Type5));
        aantalHokken.setText(""+nbrHokken);
    }

    public Event getSelectedEvent() {
        return selectedEvent;
    }
}
