package com.pharmacy.pharmacySystem.sales;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SalesPdfExporter {

    private List<Sales> listSales;
    private LocalDate reportDate;

    public SalesPdfExporter(List<Sales> listSales, LocalDate reportDate) {
        this.listSales = listSales;
        this.reportDate = reportDate;
    }

    public void export(HttpServletResponse response) throws IOException {

        response.setContentType("application/pdf");

        try(ServletOutputStream outputStream = response.getOutputStream()){
            // 1. Initialiser le PDF writer
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            document.add(new Paragraph("Rapport des Ventes")
                    .setFontSize(18).setBold().setTextAlignment(TextAlignment.CENTER));

            // AFFICHAGE DE LA DATE SÉLECTIONNÉE
            String formattedDate = reportDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            document.add(new Paragraph("Ventes du : " + formattedDate)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 2, 2, 2})).useAllAvailableWidth();

            // En-têtes
            table.addHeaderCell("Médicament");
            table.addHeaderCell("Quantité");
            table.addHeaderCell("Prix Unitaire");
            table.addHeaderCell("Total");

            // Regroupement
            Map<String, SalesReportItem> map = new TreeMap<>();
            for (Sales s : listSales) {
                map.putIfAbsent(s.getNameDrug(), new SalesReportItem(s.getNameDrug(), s.getPrixUnitaire()));
                map.get(s.getNameDrug()).addSale(s.getQuantity(), s.getPrixTotal());
            }

            // Remplissage
            for (SalesReportItem item : map.values()) {
                table.addCell(item.getDrugName());
                table.addCell(new Cell().add(new Paragraph(item.getTotalQuantity().toString())).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(item.getUnitPrice().toBigInteger().toString())).setTextAlignment(TextAlignment.RIGHT));
                table.addCell(new Cell().add(new Paragraph(item.getTotalRevenue().toBigInteger().toString())).setTextAlignment(TextAlignment.RIGHT));
            }

            //  Ligne de total vide
            table.addCell(new Cell().add(new Paragraph("TOTAL")).setBold());
            table.addCell(new Cell().add(new Paragraph("")));
            table.addCell(new Cell().add(new Paragraph("")));
            table.addCell(new Cell().add(new Paragraph("")));

            document.add(table);
            document.close();
        }
    }
}
