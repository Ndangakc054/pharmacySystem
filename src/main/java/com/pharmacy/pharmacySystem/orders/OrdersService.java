package com.pharmacy.pharmacySystem.orders;

import ch.qos.logback.classic.Logger;
import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.purchaseFiles.PurchaseFiles;
import com.pharmacy.pharmacySystem.purchaseFiles.PurchaseFilesRepository;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockRepository;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OrdersService {
    @Autowired private OrdersRepository repo;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private UsersRepository usersRepo;
    @Autowired private DrugsRepository drugsRepo;
    @Autowired private StockRepository stockRepo;
    @Autowired private PurchaseFilesRepository filesRepository;

    public List<Orders> listAll() {
        return (List<Orders>) repo.findAll();
    }

    public List<Stock> refreshOrders() {
        String sqlQuery = "SELECT * FROM inventory ORDER BY quantity ASC";
        return jdbcTemplate.query(sqlQuery,  (rs, rowNum) -> {
            Stock stock = new Stock();
            stock.setIdStock(rs.getInt("id_inventory"));
            Drugs drug = new Drugs();
            drug.setNameDrug(rs.getString("drug_name"));
            stock.setDrug(drug);
            stock.setQuantity(rs.getInt("quantity"));
            return stock;
        });
    }

    /**@Transactional
    public void saveOrders(List<OrdersDTO> ordersDTO) {
        for (OrdersDTO ordersDTO1 : ordersDTO) {
            //RECHERCHER UN MÉDICAMENT PAR SON NOM
            Optional<Drugs> optionalDrug = drugsRepo.findByNameDrug(ordersDTO1.getDrugName());
            // Vérifiez si le médicament existe
            if (optionalDrug.isEmpty()) {
                throw new RuntimeException("Médicament non trouvé : " +ordersDTO1.getDrugName());
            }
            // Récupérez l'entité Drugs de l'Optional
            Drugs drug = optionalDrug.get();
            // Créez une nouvelle commande
            Orders newOrder = new Orders();
            newOrder.setDrug(drug);
            newOrder.setMinimumThreshold(ordersDTO1.getQuantity());
            // Enregistrez la commande
            repo.save(newOrder);
        }
        // Générez le PDF
        pdfService.generatePdf(ordersDTO);
    }**/


    public PurchaseFiles saveOrders(OrdersForm ordersForm) {

        //log.info("Sauvergarde commande : {}", ordersForm);
        //Créer une liste pour stocker une commande
        List<Orders> ordersList = new ArrayList<>();

        //Parcourir les items du form pour creer les commandes
        for (OrdersItem item : ordersForm.getItems()) {
            Drugs drug = drugsRepo.findById(item.getIdDrug())
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé : " +item.getDrugName()));
            Orders order = new Orders();
            order.setDrug(drug);
            order.setMinimumThreshold(item.getQuantity());
            order.setDateOutOfStock(ordersForm.getDateOrders());

            ordersList.add(order);
        }
        //Generer le pdf de la commande
        byte[] pdfBytes;
        try {
            pdfBytes = generatePurchaseFilePdf(ordersForm);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
        //Création de la fiche de réquisition

        PurchaseFiles file = new PurchaseFiles();
        file.setDateOrders(LocalDate.now());
        file.setPdfOrders(pdfBytes);

        //Sauvegarder le fichier AVANT les commandes
        file = filesRepository.save(file);
        /**Log création fichier
        historyService.logInsert("fiche de commande", file);**/

        //Associer la fiche de commande aux commandes avant de le sauvergarder

       for (Orders order : ordersList) {
           //Associer à chaque commandes à la fiche de réquisition
            order.setPurchaseFile(file);
        }
        //Sauvergarder les commandes avant de creer la fiche
        repo.saveAll(ordersList);
      /** ordersList.forEach(order ->
               historyService.logInsert("commandes", order));**/ // Log chaque commande

        return file;

    }

    public byte[] generatePurchaseFilePdf(OrdersForm ordersForm) throws IOException {
        try{
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            //Débogage : Afficher les médicaments dans la console
            System.out.println("Nombre de médicaments dans la fiche de réquisition : " + ordersForm.getItems().size());
            for (OrdersItem item : ordersForm.getItems()) {
                System.out.println("Médicament : " + item.getDrugName() + "Quantité : " + item.getQuantity());
            }

            //Titre
            document.add(new Paragraph("PHARAMACIE WHU-TANG")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18)
                    .setBold());
            //INFORMATION SUR LA FICHE DE REQUISITION
            document.add(new Paragraph("Date : " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                    .setTextAlignment(TextAlignment.LEFT));
            document.add(new Paragraph(" "));// Espacement

            // En-tête des articles
            document.add(new Paragraph("DETAIL DES MÉDICAMENTS")
                    .setTextAlignment(TextAlignment.LEFT)
                    .setBold());
            // Tableau des médicaments
            Table table = new Table(UnitValue.createPercentArray(5)).useAllAvailableWidth();
            table.addHeaderCell("Médicament");
            table.addHeaderCell("Quantité");

            //Ajout des lignes
            for (OrdersItem item : ordersForm.getItems()) {
                table.addCell(item.getDrugName());
                table.addCell(String.valueOf(item.getQuantity()));
            }
            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
            throw e;
        }
    }

}
