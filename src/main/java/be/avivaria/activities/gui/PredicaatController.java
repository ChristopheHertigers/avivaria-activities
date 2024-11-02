package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.dao.EventDao;
import be.avivaria.activities.dao.HokDao;
import be.avivaria.activities.model.Hok;
import be.avivaria.activities.model.Event;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.ControllerRegistry;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.PersistenceController;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * User: christophe
 * Date: 03/11/13
 */
public class PredicaatController extends PersistenceController {
    private static final Logger LOGGER = LogManager.getLogger(PredicaatController.class);
    private final MainController parent;

    // View
    private JButton closeButton;
    private List<JTextField> fields;
    private List<Hok> hokken;

    private static final int NUM_ROWS = 20;

    public PredicaatController(AbstractController parentController) {
        super(new JFrame("Predicaten"), parentController);
        this.parent = (MainController)parentController;
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        Session session = getPersistenceContext();
        HokDao hokDao = new HokDao(session);
        EventDao eventDao = new EventDao(session);
        Event event = eventDao.findSelected();
        hokken = hokDao.findByEventId(event.getId());
        int nbrHokken = hokken.size();

        final JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 10, 10, 10),
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black)
        ));
        JLabel titleLabel = new JLabel(event.getNaam());
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        p.add(titleLabel, BorderLayout.WEST);
        mainWindow.getContentPane().add(p, BorderLayout.NORTH);


        int nbrColumns = (int)Math.ceil((double)nbrHokken / NUM_ROWS);
        int width = nbrColumns * 100;

        final JPanel contentPanel = new JPanel(new GridLayout(NUM_ROWS,0));
        contentPanel.setPreferredSize(new Dimension(width, 550));
        contentPanel.setBackground(mainWindow.getContentPane().getBackground());
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setAutoscrolls(true);
        scrollPane.setBorder(new EmptyBorder(0, 10, 0, 10));
        scrollPane.setBackground(mainWindow.getContentPane().getBackground());
        mainWindow.getContentPane().add(scrollPane, BorderLayout.CENTER);

        fields = new ArrayList<>();

        for (final Hok hok : hokken) {
            JTextField field = textField(hok.getHoknummer(), hok.getPredicaat());
            fields.add(field);
        }

        for (int row = 0; row < NUM_ROWS; row++) {
            for (int col = 0; col < nbrColumns; col++) {
                int index = row + (col * NUM_ROWS);
                if (index < fields.size()) {
                    JTextField field = fields.get(index);
                    contentPanel.add(label(field.getName()));
                    contentPanel.add(field);
                } else {
                    contentPanel.add(new JLabel());
                    contentPanel.add(new JLabel());
                }
            }
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        closeButton = new JButton("Sluit");
        registerAction(closeButton, new DefaultAction("close") {
            public void actionPerformed(ActionEvent e) {
                mainWindow.setVisible(false);
                mainWindow.dispose();
            }
        });
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(closeButton);
        mainWindow.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        mainWindow.setFocusTraversalPolicy(new FocusTraversalPolicy() {
            @Override
            public Component getComponentAfter(Container aContainer, Component aComponent) {
                int hoknr = Integer.valueOf(aComponent.getName());
                if (hoknr == fields.size()) return getFirstComponent(aContainer);
                return fields.get(hoknr);
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                int hoknr = Integer.valueOf(aComponent.getName());
                if (hoknr-2 < 0) return getLastComponent(aContainer);
                return fields.get(hoknr-2);
            }

            @Override
            public Component getFirstComponent(Container aContainer) {
                return fields.get(0);
            }

            @Override
            public Component getLastComponent(Container aContainer) {
                return fields.get(fields.size()-1);
            }

            @Override
            public Component getDefaultComponent(Container aContainer) {
                return fields.get(0);
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addPropertyChangeListener("focusOwner", new PropertyChangeListener() {
                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (!(evt.getNewValue() instanceof JComponent)) {
                            return;
                        }
                        JComponent focused = (JComponent) evt.getNewValue();
                        if (contentPanel.isAncestorOf(focused)) {
                            Rectangle rect = focused.getBounds();
                            rect.x = rect.x - 100 < 0 ? 0 : rect.x - 100;
                            rect.width = rect.width + 200;
                            contentPanel.scrollRectToVisible(rect);
                        }
                    }
                });


        // Display the window
        mainWindow.setMinimumSize(new Dimension(850, 700));
        mainWindow.setPreferredSize(new Dimension(850, 700));
        mainWindow.setLocation(350, 0);
        mainWindow.setVisible(true);

        // initial display
        fields.get(0).requestFocus();
    }

    @Override
    protected void doDispose() {
        getView().setVisible(false);
        closeButton = null;
        fields = null;
        hokken = null;
        ControllerRegistry.getInstance().unregister(this);
        ((JFrame)getView()).dispose();
    }

    private JLabel label(String label) {
        return new JLabel(label + " ", SwingConstants.RIGHT);
    }

    private JTextField textField(Long hoknummer, String value) {
        JTextField textField = new JTextField(value);
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setName(""+hoknummer);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField)e.getComponent();
                int hoknummer = Integer.valueOf(field.getName());
                Hok hok = hokken.get(hoknummer-1);
                if (hoknummer != hok.getHoknummer()) throw new RuntimeException("Kan overeenkomstig hok niet vinden");
                String value = field.getText();
                if (StringUtils.isBlank(value)) value = null;
                if (value == null) {
                    if (hok.getPredicaat() != null) {
                        updatePredicaat(hok, value); //System.out.println("updating");
                    } //else { System.out.println("nothing changed"); }
                } else {
                    if (!value.equals(hok.getPredicaat())) {
                        updatePredicaat(hok, value); //System.out.println("updating");
                    } //else { System.out.println("nothing changed"); }
                }

            }
        });
        return textField;
    }

    private void updatePredicaat(Hok hok, String predicaat) {
        Session session = getPersistenceContext();
        HokDao hokDao = new HokDao(session);
        hok.setPredicaat(predicaat);
        Transaction transaction = session.getTransaction();
        try {
            transaction.begin();
            hokDao.saveOrUpdate(hok);
            hokDao.flush();
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }
}
