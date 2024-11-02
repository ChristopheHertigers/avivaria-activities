package be.avivaria.activities.gui;

import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.DefaultAction;
import net.miginfocom.swing.MigLayout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;

@Component
public class AboutController extends AbstractController {

    private final JButton closeButton;

    public AboutController(@Value("${app.version}") String appVersion) {
        super(new JFrame("Over"));
        final JFrame mainWindow = (JFrame) getView();

        // View components
        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel();
        label.setText(
                "<html>" +
                "<h2><b>Avivaria Activiteiten " + appVersion + "</b></h2>" +
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
        closeButton = createCloseButton();
        buttonPanel.add(closeButton, "east");
        detailPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Assemble the view
        mainWindow.getContentPane().add(detailPanel, BorderLayout.CENTER);

        // Display the window
        mainWindow.setMinimumSize(new Dimension(300, 200));
        mainWindow.setPreferredSize(new Dimension(300, 200));
        mainWindow.setLocation(350, 50);
    }

    @Override
    public void show() {
        super.show();
        // initial display
        closeButton.requestFocus();
    }

    private JButton createCloseButton() {
        JButton closeButton = new JButton("Sluit");
        registerAction(closeButton, new DefaultAction("close") {
            public void actionPerformed(ActionEvent e) {
                close();
            }
        });
        return closeButton;
    }

}
