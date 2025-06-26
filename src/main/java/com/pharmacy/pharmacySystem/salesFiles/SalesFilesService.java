package com.pharmacy.pharmacySystem.salesFiles;

import com.itextpdf.html2pdf.HtmlConverter;
//import com.itextpdf.io.exceptions.IOException;
import java.io.IOException;
import com.itextpdf.io.font.constants.StandardFonts;
//import com.itextpdf.io.source.ByteArrayOutputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class SalesFilesService {
    @Autowired SalesFilesRepository repo;

    public byte[] generateAndSaveSalesPdf(List<SalesFilesDTO> salesFilesDTOList) throws  java.io.IOException {
        System.out.println("Nombres d'éléments reçus : " + salesFilesDTOList.size()); //Debug
        if (salesFilesDTOList.isEmpty()) {
            throw new IllegalArgumentException("Aucune donnée reçue !");
        }
        // 1.Génération du pdf
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(boas));
        Document document = new Document(pdf);
         //Ajouter un titre
        document.add(new Paragraph("Fiche de gestion journalière")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18));

        //Création du tableau
        Table table = new Table(4);
        table.setWidth(100);
        //En-têtes du tableau
        table.addHeaderCell("Produit");
        table.addHeaderCell("Quantité");
        table.addHeaderCell("PU");
        table.addHeaderCell("PT");

        //Remplir les données
        for (SalesFilesDTO sale : salesFilesDTOList) {
            table.addCell(sale.getName());
            table.addCell(sale.getQuantite());
            table.addCell(sale.getPu());
            table.addCell(sale.getPt());
        }
        document.add(table);

        //Le bas de la page
        document.add(new Paragraph("\nFait le : " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setTextAlignment(TextAlignment.RIGHT));
        document.close();

        //Sauvergarder dans la BD
        SalesFiles pdfFile = new SalesFiles();
        pdfFile.setFileName("ventes_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".pdf");
        pdfFile.setPdfContent(boas.toByteArray());
        pdfFile.setUploadDate(LocalDateTime.now());
        repo.save(pdfFile);
        return boas.toByteArray();
    }
    public byte[] generateRecentSalePdf(String htmlContent) throws IOException {
        // 1. Convertir HTML en PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos)){
            PdfDocument pdfDoc = new PdfDocument(writer);
            HtmlConverter.convertToPdf(htmlContent, baos);
            pdfDoc.close();
        }

        // 2. Sauvergarder dans la BD
        SalesFiles file = new SalesFiles();
        file.setFileName("ventes_"+LocalDateTime.now()+".pdf");
        file.setPdfContent(baos.toByteArray());
        file.setUploadDate(LocalDateTime.now());
        repo.save(file);

        return baos.toByteArray();
    }
}
