package be.avivaria.activities.gui;

import be.avivaria.activities.dao.EventRepository;
import be.avivaria.activities.dao.HokRepository;
import be.avivaria.activities.model.Event;
import be.avivaria.activities.model.Hok;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.DefaultAction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: christophe
 * Date: 03/11/13
 */
@SuppressWarnings("FieldCanBeLocal")
@Controller
public class PredicaatController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(PredicaatController.class);
    private final EventRepository eventRepository;
    private final HokRepository hokRepository;
    // View
    private JScrollPane scrollPane;
    private JPanel contentPanel;
    private final JLabel titleLabel;
    private final JButton closeButton;
    private List<JTextField> fields;
    private List<Hok> hokken;
    private Event selected;

    private static final int NUM_ROWS = 20;

    @Autowired
    public PredicaatController(
            EventRepository eventRepository,
            HokRepository hokRepository
    ) {
        super(new JFrame("Predicaten"));
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeButton.doClick();
            }
        });

        this.eventRepository = eventRepository;
        this.hokRepository = hokRepository;

        final JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 10, 10, 10),
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.black)
        ));
        titleLabel = new JLabel();
        titleLabel.setFont(new Font("Verdana", Font.BOLD, 14));
        p.add(titleLabel, BorderLayout.WEST);
        mainWindow.getContentPane().add(p, BorderLayout.NORTH);

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
                int hoknr = Integer.parseInt(aComponent.getName());
                if (hoknr == fields.size()) return getFirstComponent(aContainer);
                return fields.get(hoknr);
            }

            @Override
            public Component getComponentBefore(Container aContainer, Component aComponent) {
                int hoknr = Integer.parseInt(aComponent.getName());
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
                .addPropertyChangeListener("focusOwner", evt -> {
                    if (!(evt.getNewValue() instanceof JComponent focused)) {
                        return;
                    }
                    if (contentPanel != null && contentPanel.isAncestorOf(focused)) {
                        Rectangle rect = focused.getBounds();
                        rect.x = Math.max(rect.x - 100, 0);
                        rect.width = rect.width + 200;
                        contentPanel.scrollRectToVisible(rect);
                    }
                });


        // Display the window
        mainWindow.setMinimumSize(new Dimension(850, 700));
        mainWindow.setPreferredSize(new Dimension(850, 700));
        mainWindow.setLocation(350, 0);
    }

    @Override
    public void show() {
        selected = eventRepository.findBySelectedTrue();
        hokken = hokRepository.findAllByEventOrderByHoknummer(selected);
        int nbrHokken = hokken.size();

        titleLabel.setText(selected.getNaam());

        int nbrColumns = (int)Math.ceil((double)nbrHokken / NUM_ROWS);
        int width = nbrColumns * 100;

        final JFrame mainWindow = (JFrame) getView();
        contentPanel = new JPanel(new GridLayout(NUM_ROWS,0));
        contentPanel.setPreferredSize(new Dimension(width, 550));
        contentPanel.setBackground(mainWindow.getContentPane().getBackground());
        scrollPane = new JScrollPane(contentPanel);
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

        super.show();

        // initial display
        fields.get(0).requestFocus();

    }

    @Override
    public void dispose() {
        getView().setVisible(false);
        final JFrame mainWindow = (JFrame) getView();
        mainWindow.getContentPane().remove(scrollPane);

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
                int hoknummer = Integer.parseInt(field.getName());
                Hok hok = hokken.get(hoknummer-1);
                if (hoknummer != hok.getHoknummer()) throw new RuntimeException("Kan overeenkomstig hok niet vinden");
                String value = field.getText();
                if (StringUtils.isBlank(value)) value = null;
                if (value == null) {
                    if (hok.getPredicaat() != null) {
                        updatePredicaat(hok, null);
                    }
                } else {
                    if (!value.equals(hok.getPredicaat())) {
                        updatePredicaat(hok, value);
                    }
                }

            }
        });
        return textField;
    }

    private void updatePredicaat(Hok hok, String predicaat) {
        hok.setPredicaat(predicaat);
        try {
            hokRepository.save(hok);
        } catch (Exception e) {
            logger.error("Error updating predicaat", e);
            throw new RuntimeException(e);
        }
    }
}
