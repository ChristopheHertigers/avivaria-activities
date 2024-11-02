package be.avivaria.activities;

import be.avivaria.activities.database.DatabaseManager;
import be.indigosolutions.framework.AbstractController;

import javax.swing.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Executable.
 *
 * @author Christophe Hertigers
 * @version $Id: $
 * @date 5-jul-2008
 */
public class Main {

    public static AbstractController applicationController;

    public static void main(String[] args) throws SQLException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {

        // Database setup
        DatabaseManager.initDatabase();

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("SHUTTING DOWN DATABASE");
                try {
                    DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } catch (SQLException e) {
                    // ignore exception
                }
            }
        });

        // Set look and feel
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> applicationController = new MainController());
    }



}
