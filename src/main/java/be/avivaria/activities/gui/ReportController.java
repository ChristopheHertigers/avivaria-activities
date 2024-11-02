package be.avivaria.activities.gui;

import be.avivaria.activities.model.Event;
import be.avivaria.activities.reports.ReportDataFactory;
import be.indigosolutions.framework.ControllerRegistry;
import be.indigosolutions.framework.util.CollectionUtils;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

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
public class ReportController {
    private static final Logger LOGGER = LogManager.getLogger(ReportController.class);
    private static Map<String, JasperViewer> REPORT_WINDOWS = new HashMap<>();

    public static void closeAllReports() {
        for (String reportName : REPORT_WINDOWS.keySet()) {
            JasperViewer viewer = REPORT_WINDOWS.get(reportName);
            if (viewer != null) {
                viewer.dispose();
            }
        }
    }

    public static void closeReports(String... reportNames) {
        if (reportNames != null && reportNames.length == 0) {
            for (String reportName : reportNames) {
                JasperViewer viewer = REPORT_WINDOWS.get(reportName);
                if (viewer != null) {
                    viewer.dispose();
                }
            }
        }
    }

    private static void showReport(final String reportName, Map<String,Object> parameters, List data, Session session) {
        try {
            boolean fromSrc = false;
            String reportSrc = "src/main/jasperreports/be/avivaria/activities/reports/"+reportName+".jrxml";
            Event event = ReportDataFactory.getActiveEvent(session);
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
                LOGGER.debug("Compiling " + reportName + " from source");
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
//            setExportOptions(viewer);
            viewer.setVisible(true);
            REPORT_WINDOWS.put(reportName, viewer);
        } catch (JRException e) {
            throw new RuntimeException(e);
        } finally {
            session.close();
        }
    }

//    @SuppressWarnings("unchecked")
//    private static void setExportOptions(JasperViewer viewer) {
//        Field jrViewerField;
//        List<Class<? extends JRSaveContributor>> ALLOWED = Arrays.asList(
//                JRPdfSaveContributor.class,
//                JRDocxSaveContributor.class,
//                JRSingleSheetXlsSaveContributor.class,
//                JRMultipleSheetsXlsSaveContributor.class
//        );
//        try {
//            jrViewerField = viewer.getClass().getDeclaredField("viewer");
//            jrViewerField.setAccessible(true);
//            JRViewer jrViewer = (JRViewer) jrViewerField.get(viewer);
//            List<JRSaveContributor> savers = Arrays.asList(jrViewer.getSaveContributors());
//            for (JRSaveContributor sc : savers) {
//                if (! ALLOWED.contains(sc.getClass())) {
//                    jrViewer.removeSaveContributor(sc);
//                }
//            }
//        } catch (Exception ex) {
//            throw new RuntimeException(ex);
//        }
//    }

    private static Map<String,Object> parameters(KeyValue... keyValues) {
        Map<String,Object> p = new HashMap<>();
        if (keyValues == null || keyValues.length == 0) return p;
        for (KeyValue keyValue : keyValues) {
            p.put(keyValue.key, keyValue.value);
        }
        return p;
    }

    public static void showDeelnemerReport() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("deelnemers",
                parameters(
                        new KeyValue("REPORT_PAGE_HEADER_TITLE","Deelnemer"),
                        new KeyValue("REPORT_PAGE_FOOTER_TITLE","Lijst Deelnemers")
                ),
                ReportDataFactory.getDeelnemersForActiveEvent(session),
                session);
    }

    public static void showJeugdDeelnemerReport() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("deelnemers",
                parameters(
                        new KeyValue("REPORT_PAGE_HEADER_TITLE", "Deelnemer"),
                        new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst Jeugddeelnemers (-18 jaar)")
                ),
                ReportDataFactory.getJeugdDeelnemersForActiveEvent(session),
                session);
    }

    public static void showInschrijvingenReport() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("inschrijvers", parameters(
                        new KeyValue("LABEL_DIEREN", "Dieren"),
                        new KeyValue("LABEL_PALMARES", "Palmares"),
                        new KeyValue("LABEL_DIERENTEKOOP", "Hokken"),
                        new KeyValue("LABEL_LIDGELD", "Lidgeld"),
                        new KeyValue("LABEL_LIDGELDJEUGD", "Lidgeld (-18)"),
                        new KeyValue("LABEL_FOKKERSKAART", "Fokkerskaart"),
                        new KeyValue("LABEL_FOKKERSKAART2", "Fokkerskaart (2e)")
                ),
                ReportDataFactory.getInschrijvingenForActiveEvent(session),
                session);
    }

    public static void showPalmares() {
        PredicaatController controller = ControllerRegistry.getInstance().get(PredicaatController.class);
        if (controller != null) {
            controller.dispose();
            ControllerRegistry.getInstance().unregister(PredicaatController.class);
        }
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("palmares",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Palmares")),
                ReportDataFactory.getPalmaresForActiveEvent(session),
                session);
    }

    public static void showVerenigingenReport() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("verenigingen",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst verenigingen")),
                ReportDataFactory.getVerenigingenForActiveEvent(session),
                session);
    }

    public static void showHokLabels() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("hoklabels",
                parameters(
                        new KeyValue("LABEL_HOKNUMMER", "Hoknr."),
                        new KeyValue("LABEL_FOKKERNUMMER", "Fokkernummer:")
                ),
                ReportDataFactory.getHokLabelsForActiveEvent(session),
                session);
    }

    public static void showPrijsLabels() {
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("prijslabels",
                parameters(
                        new KeyValue("LABEL_HOKNUMMER", "Hoknr."),
                        new KeyValue("LABEL_FOKKERNUMMER", "Nr:"),
                        new KeyValue("LABEL_RAS", "Ras:"),
                        new KeyValue("LABEL_KLEUR", "Kleur:")
                ),
                ReportDataFactory.getPrijsLabelsForActiveEvent(session),
                session);
    }

    public static void showKampioenen() {
        PredicaatController controller = ControllerRegistry.getInstance().get(PredicaatController.class);
        if (controller != null) {
            controller.dispose();
            ControllerRegistry.getInstance().unregister(PredicaatController.class);
        }
        Session session = ReportDataFactory.getPersistenceContext();
        showReport("kampioenen",
                parameters(new KeyValue("REPORT_PAGE_FOOTER_TITLE", "Lijst kampioenen")),
                ReportDataFactory.getKampioenenForActiveEvent(session),
                session);
    }

    private static String getEventName(Event event) {
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
