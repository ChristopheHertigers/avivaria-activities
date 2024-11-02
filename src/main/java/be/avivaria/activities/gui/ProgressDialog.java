package be.avivaria.activities.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * User: christophe
 * Date: 29/10/13
 */
public class ProgressDialog extends JDialog implements ActionListener {

    private JProgressBar progressBar;
    private JButton button;
    private JLabel label;

    public ProgressDialog(JFrame parent, String title, String message) {
        super(parent, title, false);

        if (parent != null) {
            Dimension parentSize = parent.getSize();
            Point p = parent.getLocation();
            setLocation(p.x + parentSize.width / 8, p.y + parentSize.height / 4);
        }

        JPanel messagePane = new JPanel();
        label = new JLabel(message, createImageIcon(), JLabel.CENTER);
        label.setMinimumSize(new Dimension(550, 50));
        label.setPreferredSize(new Dimension(550, 50));
        messagePane.add(label, BorderLayout.NORTH);
        getContentPane().add(messagePane, BorderLayout.NORTH);

        JPanel progressPane = new JPanel();
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setMinimumSize(new Dimension(550, 30));
        progressBar.setPreferredSize(new Dimension(550, 30));
        progressPane.add(progressBar);
        getContentPane().add(progressPane);

        JPanel buttonPane = new JPanel();
        button = new JButton("OK");
        button.setEnabled(false);
        buttonPane.add(button);
        button.addActionListener(this);
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(600, 200));
        setPreferredSize(new Dimension(600, 200));
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        setVisible(false);
        dispose();
    }

    public void setButtonEnabled(boolean enabled) {
        if (button != null) button.setEnabled(enabled);
    }

    public void setProgress(long progress) {
        if (progressBar != null) {
            progressBar.setValue(new Long(progress).intValue());
            progressBar.setString(progress + "%");
        }
    }

    public void setMessage(String text, boolean showIcon) {
        if (label != null) {
            label.setText(text);
            if (showIcon) {
                label.setIcon(createImageIcon());
            } else {
                label.setIcon(null);
            }
        }
    }

    private ImageIcon createImageIcon() {
        URL imgURL = getClass().getResource("waitAnimation.gif");
        if (imgURL != null) {
            return new ImageIcon(imgURL, "Wait animation");
        } else {
            System.err.println("Couldn't find file: waitAnimation.gif");
            return null;
        }
    }
}
