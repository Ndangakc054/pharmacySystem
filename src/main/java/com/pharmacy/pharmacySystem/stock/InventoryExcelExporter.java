package com.pharmacy.pharmacySystem.stock;

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

public class InventoryExcelExporter {

    private XSSFWorkbook workbook;
    private Sheet sheet;
    private List<Stock> listStock;

    public InventoryExcelExporter(List<Stock> listStock) {
        this.listStock = listStock;
        workbook = new XSSFWorkbook();
    }

    private void writeHeaderLine() {
        sheet = workbook.createSheet("INVENTAIRE");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(12);
        style.setFont(font);

        // Création des colonnes demandées
        createCell(row, 0, "Nom du Médicament", style);
        createCell(row, 1, "Quantité", style);
        createCell(row, 2, "Date d'expiration", style);
        createCell(row, 3, "Prix Unitaire", style); // Colonne vide demandée
        createCell(row, 4, "Prix Total", style);// Colonne vide demandée
        createCell(row, 5, "Date d'entrée", style);
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

        for (Stock stock : listStock) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            // 1. Nom du médicament (via la relation Drug)
            // Assure-toi que stock.getDrug() n'est pas null
            createCell(row, columnCount++, stock.getDrug().getNameDrug(), style);

            // 2. Quantité
            createCell(row, columnCount++, stock.getQuantity(), style);

            // 3. Date d'expiration (Vide comme demandé)
            createCell(row, columnCount++, "", style);

            // 4. Prix Unitaire (Vide comme demandé)
            createCell(row, columnCount++, "", style);

            // 5. Prix Total (Vide comme demandé)
            createCell(row, columnCount++, "", style);

            // 6. Date d'entrée (Vide comme demandé)
            createCell(row, columnCount++, "", style);
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
