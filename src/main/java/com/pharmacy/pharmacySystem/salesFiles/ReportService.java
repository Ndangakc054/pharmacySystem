package com.pharmacy.pharmacySystem.salesFiles;

//import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.user.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {
    @Autowired SalesFilesRepository pdfFilesRepo;

    public byte[] generateDailyReport(Users vendor, List<Sales> sales) throws IOException {
        System.out.println("[DEBUG] Début génération PDF pour " +vendor.getNomUtilisateur());
        System.out.println("[DEBUG] Nombre de ventes trouvées: " +sales.size());
        //Création du pdf
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
        Document document = new Document(pdf);

        //Header
        document.add(new Paragraph("Rapport Journalier de Ventes")
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER));

        // Infos vendeur
        document.add(new Paragraph("Vendeur: " + vendor.getNomUtilisateur()));
        document.add(new Paragraph("Date: " + LocalDate.now().format(DateTimeFormatter.ISO_DATE)));

        // Tableau des ventes
        Table table = new Table(4);
        table.addHeaderCell("Médicament");
        table.addHeaderCell("Quantité");
        table.addHeaderCell("Prix Unitaire");
        table.addHeaderCell("Total");

        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (Sales sale : sales) {
            table.addCell(sale.getNameDrug());
            table.addCell(String.valueOf(sale.getQuantity()));
            table.addCell(sale.getPrixUnitaire().toString());
            table.addCell(sale.getPrixTotal().toString());

            totalGeneral = totalGeneral.add(sale.getPrixTotal());
        }
        document.add(table);

        //Total général
        document.add(new Paragraph("Total des recettes : " + totalGeneral.toString() + "DH")
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT));
        document.close();

        //Sauvergarder dans pdf_files

        SalesFiles salesFile = new SalesFiles();
        salesFile.setFileName("rapport_"+vendor.getNomUtilisateur()+"_"+LocalDate.now()+".pdf");
        salesFile.setPdfContent(baos.toByteArray());
        salesFile.setUploadDate(LocalDateTime.now());
        pdfFilesRepo.save(salesFile);

        System.out.println("[DEBUG] PDF généré avec succès. Taille: " +baos.size() + "bytes");
        return baos.toByteArray();
    }
}
