package be.avivaria.activities;

import be.avivaria.activities.database.DatabaseManager;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan({"be.avivaria.activities", "be.indigosolutions.framework"})
public class ActivitiesApplication {

    public ActivitiesApplication() {
        initUI();
    }

    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(ActivitiesApplication.class)
                .headless(false)
                .web(WebApplicationType.NONE)
                .run(args);
        EventQueue.invokeLater(() -> {
            var db = ctx.getBean(DatabaseManager.class);
            db.initDatabase();

            MainController main = ctx.getBean(MainController.class);
            main.show();
        });
    }

    private void initUI() {

    }
}
