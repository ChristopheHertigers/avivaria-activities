package be.avivaria.activities.gui;

import be.avivaria.activities.MainController;
import be.avivaria.activities.reports.InschrijvingReportDecorator;
import be.avivaria.activities.reports.ReportDataFactory;
import be.indigosolutions.framework.AbstractController;
import be.indigosolutions.framework.PersistenceController;
import be.indigosolutions.framework.util.EnvironmentUtil;
import be.indigosolutions.framework.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * @author ch
 */
public class ExportController extends PersistenceController {
    private static final Logger LOGGER = LogManager.getLogger(ExportController.class);
    private static final String SEP = EnvironmentUtil.getDirSeparator();

    private final MainController parent;

    public ExportController(AbstractController parentController) {
        super(parentController);
        this.parent = (MainController) parentController;

        Session session = getPersistenceContext();
        List<InschrijvingReportDecorator> inschrijvingen = ReportDataFactory.getInschrijvingenForActiveEvent(session);
        Iterator<InschrijvingReportDecorator> it = inschrijvingen.iterator();
        long previousInschrijvingId = -999L;
        // remove duplicates (which are necessary for the report, but not here)
        while (it.hasNext()) {
            InschrijvingReportDecorator next = it.next();
            if (next.getInschrijvingId() == previousInschrijvingId) it.remove();
            else previousInschrijvingId = next.getInschrijvingId();
        }

        File desktop = EnvironmentUtil.getDesktopDirectory();

        try {
            Workbook wb = new XSSFWorkbook();
            Sheet sheet1 = wb.createSheet("Inschrijvingen");
            CellStyle bold = createBoldStyle(wb);
            CellStyle decimal = createDecimalStyle(wb);
            int rowCount = 0;
            Row headerRow = sheet1.createRow(rowCount++);
            Cell cell = headerRow.createCell(0); cell.setCellValue("Naam"); cell.setCellStyle(bold);
            cell = headerRow.createCell(1); cell.setCellValue("Adress"); cell.setCellStyle(bold);
            cell = headerRow.createCell(2); cell.setCellValue("Dieren"); cell.setCellStyle(bold);
            cell = headerRow.createCell(3); cell.setCellValue("Palmares"); cell.setCellStyle(bold);
            cell = headerRow.createCell(4); cell.setCellValue("Dieren te koop"); cell.setCellStyle(bold);
            cell = headerRow.createCell(5); cell.setCellValue("Lidgeld"); cell.setCellStyle(bold);
            cell = headerRow.createCell(6); cell.setCellValue("Lidgeld (-18)"); cell.setCellStyle(bold);
            cell = headerRow.createCell(7); cell.setCellValue("Fokkerskaart"); cell.setCellStyle(bold);
            cell = headerRow.createCell(8); cell.setCellValue("Fokkerskaart (2e)"); cell.setCellStyle(bold);
            cell = headerRow.createCell(9); cell.setCellValue("Totaal"); cell.setCellStyle(bold);

            for (InschrijvingReportDecorator inschrijving : inschrijvingen) {
                Row row = sheet1.createRow(rowCount++);
                row.createCell(0).setCellValue(inschrijving.getDeelnemerNaam());
                row.createCell(1).setCellValue(inschrijving.getDeelnemerStraat() + " " + inschrijving.getDeelnemerWoonplaats());
                cell = row.createCell(2, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalDier())); cell.setCellStyle(decimal);
                cell = row.createCell(3, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalPalmares())); cell.setCellStyle(decimal);
                cell = row.createCell(4, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalDierTeKoop())); cell.setCellStyle(decimal);
                cell = row.createCell(5, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalLidgeld())); cell.setCellStyle(decimal);
                cell = row.createCell(6, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalLidgeldJeugd())); cell.setCellStyle(decimal);
                cell = row.createCell(7, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalFokkerskaart())); cell.setCellStyle(decimal);
                cell = row.createCell(8, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaalFokkerskaart2())); cell.setCellStyle(decimal);
                cell = row.createCell(9, Cell.CELL_TYPE_NUMERIC); cell.setCellValue(d(inschrijving.getTotaal())); cell.setCellStyle(decimal);
            }

            FileOutputStream out = new FileOutputStream(desktop.getAbsolutePath() + SEP + "inschrijvingen.xlsx");
            wb.write(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(parent.getView(), "Export inschrijvingen succesvol aangemaakt.", "Succes", JOptionPane.INFORMATION_MESSAGE);
    }

    private CellStyle createBoldStyle(Workbook wb) {
        Font boldFont = wb.createFont();
        boldFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        CellStyle bold = wb.createCellStyle();
        bold.setFont(boldFont);
        return bold;
    }

    private CellStyle createDecimalStyle(Workbook wb) {
        DataFormat format = wb.createDataFormat();
        CellStyle style = wb.createCellStyle();
        style.setDataFormat(format.getFormat("0.00"));
        return style;
    }

    private Double d(String value) {
        return Double.parseDouble(StringUtils.replace(value, ",", "."));
    }
}
