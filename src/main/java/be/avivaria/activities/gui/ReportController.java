package be.avivaria.activities.gui;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.reports.ReportDataFactory;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.util.CollectionUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * User: christophe
 * Date: 02/11/13
 */
@Controller
public class ReportController extends AbstractController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final Map<String, JasperViewer> REPORT_WINDOWS = new HashMap<>();

    private final ReportDataFactory reportDataFactory;

    @Autowired
    public ReportController(ReportDataFactory reportDataFactory) {
        this.reportDataFactory = reportDataFactory;
    }

    public void closeAllReports() {
        for (String reportName : REPORT_WINDOWS.keySet()) {
            JasperViewer viewer = REPORT_WINDOWS.get(reportName);
            if (viewer != null) {
                viewer.dispose();
            }
        }
    }

    public void closeReports(String... reportNames) {
        if (reportNames != null && reportNames.length == 0) {
            for (String reportName : reportNames) {
                JasperViewer viewer = REPORT_WINDOWS.get(reportName);
                if (viewer != null) {
                    viewer.dispose();
                }
            }
        }
    }

    private void showReport(final String reportName, Map<String,Object> parameters, List data) {
        try {
            boolean fromSrc = false;
            String reportSrc = "src/main/jasperreports/be/avivaria/activities/reports/"+reportName+".jrxml";
            Event event = reportDataFactory.getActiveEvent();
            Map<String,Object> defaultParameters = parameters(
                    new KeyValue("EVENT_NAME", getEventName(event)),
                    new KeyValue("EVENT_ADDRESS","HEKSENBERGSTRAAT 5-7   HASSELT"),
                    new KeyValue("EVENT_ORGANIZER","KLEINDIERCLUB AVIVARIA VZW."),
                    new KeyValue("LABEL_CLUB", event.getClubNaamRegel1()),
                    new KeyValue("LABEL_CLUBNAAM", event.getClubNaamRegel2())
            );
            if (CollectionUtils.isNotEmpty(parameters)) defaultParameters.putAll(parameters);
            JasperReport jasperReport;
            if (fromSrc) {
                logger.debug("Compiling " + reportName + " from source");
                jasperReport = JasperCompileManager.compileReport(reportSrc);
            } else {
                URL jasperResURL = ReportDataFactory.class.getResource(reportName+".jasper");
                jasperReport = (JasperReport) JRLoader.loadObject(jasperResURL);
            }
            if (REPORT_WINDOWS.get(reportName) != null) {
                REPORT_WINDOWS.get(reportName).dispose();
            }
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, defaultParameters, new JRBeanCollectionDataSource(data));
            JasperViewer viewer = new JasperViewer(jasperPrint, false, new Locale("nl","BE"));
            viewer.setExtendedState(viewer.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            viewer.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    JasperViewer viewer = REPORT_WINDOWS.get(reportName);
                    if (viewer != null) {
                        REPORT_WINDOWS.remove(reportName);
                    }
                }
            });
            viewer.setVisible(true);
            REPORT_WINDOWS.put(reportName, viewer);
        } catch (JRException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String,Object> parameters(KeyValue... keyValues) {
        Map<String,Object> p = new HashMap<>();
        if (keyValues == null) return p;
        for (KeyValue keyValue : keyValues) {
            p.put(keyValue.key, keyValue.value);
        }
        return p;
    }

    public void showDeelnemerReport() {
        showReport("deelnemers",
                parameters(
                        new KeyValue("REPORT_PAGE_HEADER_TITLE","Deelnemer"),
                        new KeyValue("REPORT_PAGE_FOOTER_TITLE","Lijst Deelnemers")
                ),
                reportDataFactory.getDeelnemersForActiveEvent());
    }

    public void showJeugdDeelnemerReport() {
        showReport("deelnemers",
                parameters(
                        new KeyValue("REPORT_PAGE_HEADER_TITLE", "Deelnemer"),
                        new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst Jeugddeelnemers (-18 jaar)")
                ),
                reportDataFactory.getJeugdDeelnemersForActiveEvent());
    }

    public void showInschrijvingenReport() {
        showReport("inschrijvers", parameters(
                        new KeyValue("LABEL_DIEREN", "Dieren"),
                        new KeyValue("LABEL_PALMARES", "Palmares"),
                        new KeyValue("LABEL_DIERENTEKOOP", "Hokken"),
                        new KeyValue("LABEL_LIDGELD", "Lidgeld"),
                        new KeyValue("LABEL_LIDGELDJEUGD", "Lidgeld (-18)"),
                        new KeyValue("LABEL_FOKKERSKAART", "Fokkerskaart"),
                        new KeyValue("LABEL_FOKKERSKAART2", "Fokkerskaart (2e)")
                ),
                reportDataFactory.getInschrijvingenForActiveEvent());
    }

    public void showPalmares() {
        showReport("palmares",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Palmares")),
                reportDataFactory.getPalmaresForActiveEvent());
    }

    public void showVerenigingenReport() {
        showReport("verenigingen",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst verenigingen")),
                reportDataFactory.getVerenigingenForActiveEvent());
    }

    public void showHokLabels() {
        showReport("hoklabels",
                parameters(
                        new KeyValue("LABEL_HOKNUMMER", "Hoknr."),
                        new KeyValue("LABEL_FOKKERNUMMER", "Fokkernummer:")
                ),
                reportDataFactory.getHokLabelsForActiveEvent());
    }

    public void showPrijsLabels() {
        showReport("prijslabels",
                parameters(
                        new KeyValue("LABEL_HOKNUMMER", "Hoknr."),
                        new KeyValue("LABEL_FOKKERNUMMER", "Nr:"),
                        new KeyValue("LABEL_RAS", "Ras:"),
                        new KeyValue("LABEL_KLEUR", "Kleur:")
                ),
                reportDataFactory.getPrijsLabelsForActiveEvent());
    }

    public void showKampioenen() {
        showReport("kampioenen",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst kampioenen")),
                reportDataFactory.getKampioenenForActiveEvent());
    }

    public void showAantalDierenLidAvivaria() {
        showReport("aantaldieren",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Aantal dieren lid Avivaria")),
                reportDataFactory.getAantalDieren(true));
    }

    public void showAantalDierenNietLidAvivaria() {
        showReport("aantaldieren",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Aantal dieren niet-leden")),
                reportDataFactory.getAantalDieren(false));
    }

    private String getEventName(Event event) {
        String naam = event.getNaam();
        if (naam.startsWith("PROVINCIALE WEDSTRIJD LIMBURG EN ")) {
            naam = "PROVINCIALE WEDSTRIJD LIMBURG EN\n" + naam.replace("PROVINCIALE WEDSTRIJD LIMBURG EN ", "");
        }
        return naam;
    }

    private static class KeyValue {
        String key;
        String value;

        private KeyValue(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

}
