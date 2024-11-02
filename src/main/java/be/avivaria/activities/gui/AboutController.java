package be.avivaria.activities.gui;

import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.DefaultAction;
import be.indigosolutions.framework.util.SystemConfiguration;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;

public class AboutController extends AbstractController {

    public AboutController(AbstractController parentController) {
        super(new JFrame("Over"), parentController);
        final JFrame mainWindow = (JFrame) getView();

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel();
        label.setText(
                "<html>" +
                "<h2><b>Avivaria Activiteiten " + getVersion() + "</b></h2>" +
                "<p></p>" +
                "<p><b>JVM:</b> " + System.getProperty("java.version") + "</p>" +
                "<p></p>" +
                "<p></p>" +
                "</html>"
        );
        infoPanel.add(label, BorderLayout.CENTER);
        detailPanel.add(infoPanel, BorderLayout.CENTER);

        // Button panel
        int year = LocalDateTime.now().getYear();
        JPanel buttonPanel = new JPanel(new MigLayout("", "[grow][]"));
        buttonPanel.add(new JLabel("<html><p>&copy; 2008-"+year+" Christophe Hertigers</p></html>"));
        JButton closeButton = createCloseButton(mainWindow);
        buttonPanel.add(closeButton, "east");
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assemble the view
        mainWindow.getContentPane().add(detailPanel, BorderLayout.CENTER);

        // Display the window
        mainWindow.setMinimumSize(new Dimension(300, 200));
        mainWindow.setPreferredSize(new Dimension(300, 200));
        mainWindow.setLocation(350, 50);
        mainWindow.setVisible(true);

        // initial display
        closeButton.requestFocus();
    }

    private String getVersion() {
        return SystemConfiguration.AppVersion.getValue().replace("-SNAPSHOT","");
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

}
