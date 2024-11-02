package be.avivaria.activities;

import be.avivaria.activities.dao.EventDao;
import be.avivaria.activities.dao.HokDao;
import be.avivaria.activities.dao.InschrijvingDao;
import be.avivaria.activities.gui.ActivityMenuItem;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.HokType;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.PersistenceController;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

/**
 * Main application controller.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @created 21-jul-2008
 */
public class MainController extends PersistenceController {

    private JLabel huidigeActiviteitLabel;

    private JPanel inschrijvingenPanel;
    private JLabel aantalInschrijvingen;
    private JButton inschrijvingDetailButton;

    private JPanel hoknummerTitlePanel;
    private JPanel hoknummerPanel;
    private JLabel aantalHokkenType1 = new JLabel();
    private JLabel aantalHokkenType2 = new JLabel();
    private JLabel aantalHokkenType3 = new JLabel();
    private JLabel aantalHokkenType4 = new JLabel();
    private JLabel aantalHokkenType5 = new JLabel();
    private JLabel aantalHokken = new JLabel();
    private JButton hoknummersButton;
    private JButton afdrukkenInschrijversButton;

    private JPanel palmaresTitlePanel;
    private JPanel palmaresPanel;
    private JButton predicatenButton;
    private JButton afdrukkenVerenigingenButton;
    private JButton afdrukkenDeelnemersButton;
    private JButton afdrukkenJeugdButton;
    private JButton afdrukkenPalmaresButton;
    private JButton afdrukkenKampioenenButton;

    private JPanel labelsTitlePanel;
    private JPanel labelsPanel;
    private JButton afdrukkenHokLabelsButton;
    private JButton afdrukkenPrijsLabelsButton;
    private JButton editPrijsLabelsButton;

    public MainController() {
        super(new JFrame("Avivaria Activiteiten Beheer"), null);
        final JFrame mainWindow = (JFrame) getView();

        // Decoration etc.
        mainWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainWindow.getContentPane().setLayout(null);

        // Menu
        JMenuBar menuBar = ActivityMenuItem.createMenu(this, getPersistenceContext());
        menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
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
        JButton exitButton = createExitButton();
        bottomPanel.add(exitButton, BorderLayout.EAST);
        mainWindow.add(bottomPanel);

        // load data
        loadData();

        // Display the window
        mainWindow.setMinimumSize(new Dimension(860, 620));
        mainWindow.setPreferredSize(new Dimension(860, 620));
        mainWindow.setLocation(300, 0);
        mainWindow.setVisible(true);
        exitButton.requestFocus();
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
        inschrijvingDetailButton = new JButton("Ga naar inschrijving");
        inschrijvingDetailButton.setBounds(670, 3, 150, 28);
        registerAction(inschrijvingDetailButton, ActivityMenuItem.Inschrijvingen.getAction(this, getPersistenceContext()));
        return inschrijvingDetailButton;
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
        huidigeActiviteitLabel = new JLabel();
        huidigeActiviteitLabel.setFont(new Font("Verdana", Font.PLAIN, 16));
        huidigeActiviteitLabel.setForeground(new Color(0, 0, 128));
        panel.add(huidigeActiviteitLabel);
        return panel;
    }

    private JPanel createInschrijvingPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 110, 820, 30);

        JLabel aantalInschrijvingenLabel = new JLabel("Aantal inschrijvingen:");
        aantalInschrijvingenLabel.setBounds(30, 0, 150, 30);
        panel.add(aantalInschrijvingenLabel);

        aantalInschrijvingen = new JLabel();
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
        hoknummersButton = new JButton("Hoknummers");
        hoknummersButton.setBounds(655, 3, 165, 28);
        registerAction(hoknummersButton, ActivityMenuItem.HoknummersToekennen.getAction(this, getPersistenceContext()));
        return hoknummersButton;
    }

    private JButton createAfdrukkenInschrijversButton() {
        afdrukkenInschrijversButton = new JButton("Afdrukken inschrijvers");
        afdrukkenInschrijversButton.setBounds(655, 33, 165, 28);
        registerAction(afdrukkenInschrijversButton, ActivityMenuItem.InschrijvingenAfdrukken.getAction(this, getPersistenceContext()));
        return afdrukkenInschrijversButton;
    }

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
        panel.setBounds(10, 190, 820, 65);

        predicatenButton = new JButton("Predicaten");
        predicatenButton.setBounds(230, 3, 90, 28);
        registerAction(predicatenButton, ActivityMenuItem.PredicatenToekennen.getAction(this, getPersistenceContext()));
        panel.add(predicatenButton);

        afdrukkenVerenigingenButton = new JButton("Afdrukken verenigingen");
        afdrukkenVerenigingenButton.setBounds(325, 3, 165, 28);
        registerAction(afdrukkenVerenigingenButton, ActivityMenuItem.VerenigingenAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenVerenigingenButton);

        afdrukkenDeelnemersButton = new JButton("Afdrukken deelnemers");
        afdrukkenDeelnemersButton.setBounds(495, 3, 160, 28);
        registerAction(afdrukkenDeelnemersButton, ActivityMenuItem.DeelnemersAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenDeelnemersButton);

        afdrukkenJeugdButton = new JButton("Afdrukken jeugd");
        afdrukkenJeugdButton.setBounds(660, 3, 160, 28);
        registerAction(afdrukkenJeugdButton, ActivityMenuItem.JeugdDeelnemersAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenJeugdButton);

        afdrukkenPalmaresButton = new JButton("Afdrukken palmares");
        afdrukkenPalmaresButton.setBounds(495, 35, 160, 28);
        registerAction(afdrukkenPalmaresButton, ActivityMenuItem.PalmaresAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenPalmaresButton);

        afdrukkenKampioenenButton = new JButton("Afdrukken kampioenen");
        afdrukkenKampioenenButton.setBounds(660, 35, 160, 28);
        registerAction(afdrukkenKampioenenButton, ActivityMenuItem.KampioenenAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenKampioenenButton);

        return panel;
    }

    private JPanel createLabelsPanel() {
        JPanel panel = new JPanel(null);
        panel.setBounds(10, 230, 820, 30);

        afdrukkenHokLabelsButton = new JButton("Afdrukken hoklabels");
        afdrukkenHokLabelsButton.setBounds(330, 3, 160, 28);
        registerAction(afdrukkenHokLabelsButton, ActivityMenuItem.HokLabelsAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenHokLabelsButton);

        afdrukkenPrijsLabelsButton = new JButton("Afdrukken prijslabels");
        afdrukkenPrijsLabelsButton.setBounds(495, 3, 160, 28);
        registerAction(afdrukkenPrijsLabelsButton, ActivityMenuItem.PrijsLabelsAfdrukken.getAction(this, getPersistenceContext()));
        panel.add(afdrukkenPrijsLabelsButton);

        editPrijsLabelsButton = new JButton("Wijzig prijslabels");
        editPrijsLabelsButton.setBounds(660, 3, 160, 28);
        registerAction(editPrijsLabelsButton, ActivityMenuItem.EditPrijsLabels.getAction(this, getPersistenceContext()));
        panel.add(editPrijsLabelsButton);

        return panel;
    }

    public void loadData() {
        Session session = getPersistenceContext();
        EventDao eventDao = new EventDao(session);
        Event selectedEvent = eventDao.findSelected();

        InschrijvingDao inschrijvingDao = new InschrijvingDao(session);
        long nbrInschrijvingen = inschrijvingDao.countForEvent(selectedEvent.getId());

        huidigeActiviteitLabel.setText(selectedEvent.getNaam());
        aantalInschrijvingen.setText(""+nbrInschrijvingen);

        HokDao hokDao = new HokDao(session);
        Map<HokType,Long> countsPerHokType = hokDao.countPerHokTypeByEventId(selectedEvent.getId());
        long nbrHokken = hokDao.countForEvent(selectedEvent.getId());

        aantalHokkenType1.setText(countsPerHokType.get(HokType.Type1) == null ? "0" : ""+countsPerHokType.get(HokType.Type1));
        aantalHokkenType2.setText(countsPerHokType.get(HokType.Type2) == null ? "0" : ""+countsPerHokType.get(HokType.Type2));
        aantalHokkenType3.setText(countsPerHokType.get(HokType.Type3) == null ? "0" : ""+countsPerHokType.get(HokType.Type3));
        aantalHokkenType4.setText(countsPerHokType.get(HokType.Type4) == null ? "0" : ""+countsPerHokType.get(HokType.Type4));
        aantalHokkenType5.setText(countsPerHokType.get(HokType.Type5) == null ? "0" : ""+countsPerHokType.get(HokType.Type5));
        aantalHokken.setText(""+nbrHokken);
    }

}
