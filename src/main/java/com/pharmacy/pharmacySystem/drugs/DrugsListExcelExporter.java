package com.pharmacy.pharmacySystem.drugs;

import com.pharmacy.pharmacySystem.stock.Stock;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;

import static org.apache.poi.ss.util.CellUtil.createCell;

public class DrugsListExcelExporter {

    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<Drugs> listDrugs;

    public DrugsListExcelExporter(List<Drugs> listDrugs){
        this.listDrugs = listDrugs;
        workbook = new XSSFWorkbook();
    }

    public void writeHeaderLine(){
        sheet = workbook.createSheet("MÉDICAMENTS");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(14);
        style.setFont(font);

        // Création des colonnes demandées
        createCell(row, 0, "Nom du Médicament", style);
        createCell(row, 1, "Catégorie", style);
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines() {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (Drugs drugs : listDrugs) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // 1. Nom du médicament (via la relation Drug)
            // Assure-toi que stock.getDrug() n'est pas null
            createCell(row, columnCount++, drugs.getNameDrug(), style);

            // 2. Quantité
            createCell(row, columnCount++, drugs.getCategorie(), style);
        }
    }

    public void export(HttpServletResponse response) throws IOException {
        writeHeaderLine();
        writeDataLines();

        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }
}
