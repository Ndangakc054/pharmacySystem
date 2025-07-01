package com.pharmacy.pharmacySystem.sales;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.pharmacy.pharmacySystem.invoices.Invoices;
import com.pharmacy.pharmacySystem.invoices.InvoicesRepository;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.stock.StockRepository;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SalesService {
    @Autowired private SalesRepository repo;
    @Autowired private DrugsRepository repoDrugs;
    @Autowired private UsersRepository repoUser;
    @Autowired private InvoicesRepository invoicesRepository;
    @Autowired private StockRepository stockRepo;
    //@Autowired private ActionsHistoryService historyService;

    public List<Sales> listAll(){
        //return (List<Sales>) repo.findAll();
        return (List<Sales>) repo.findAllSalesByDateSaleDesc();
    }
    public Invoices saveSales(SalesForm salesForm) {
        //Vérifier que la facture est vide
        if (salesForm.getItems() == null || salesForm.getItems().isEmpty()) {
            throw new RuntimeException("Impossible de valider : la facture est vide !");
        }
       // Récupérer l'utilisateur
       Users user = repoUser.findById(salesForm.getUserId())
               .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé !"));

        // Vérification du nom de l'utilisateur
        if(!user.getNomUtilisateur().equals(salesForm.getUserName())) {
            throw new RuntimeException("Incohérence détectée dans les données utilisateur !");
        }

       // Créer une liste pour stocker les ventes
       List<Sales> salesList = new ArrayList<>();

       // Parcourir les items du formulaire pour créer les ventes
       for (SalesItem item : salesForm.getItems()) {
          Drugs drug = repoDrugs.findById(item.getIdDrug())
                   .orElseThrow(() -> new RuntimeException("Médicament non trouvé : " + item.getDrugName()));
           //Vérifier le stock disponible
           Integer availableQuantity = stockRepo.getTotalQuantityByDrugName(drug.getIdDrug());
           if(availableQuantity < item.getQuantity()) {
               throw new RuntimeException("Stock insuffisant pour " + drug.getNameDrug() +
                                           ". Disponible: " + availableQuantity +
                                            ".Demandé: " + item.getQuantity());
           }
           Sales sale = new Sales();
           sale.setDrug(drug);
           sale.setNameDrug(drug.getNameDrug());
           sale.setQuantity(item.getQuantity());
           sale.setPrixUnitaire(item.getUnitPrice());
           sale.setPrixTotal(item.getTotalAmount());
           sale.setUser(user);
           sale.setUserName(user.getNomUtilisateur());
           sale.setDateVente(LocalDateTime.now());

           salesList.add(sale);
       }

        // Générer le PDF de la facture
        byte[] pdfBytes;
        try {
            pdfBytes = generateInvoicePdf(salesForm, user);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }

        // Création de la facture
        Invoices invoice = new Invoices();
        invoice.setUser(user);
        invoice.setDateVente(LocalDateTime.now());
        invoice.setPdfInvoice(pdfBytes);
        invoice.setInvoiceName("facture_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + ".pdf");

        // Sauvegarder la facture pour obtenir son ID
        invoice = invoicesRepository.save(invoice);
        /**Log pour la facture
        historyService.logInsert("factures", invoice);**/

        // Associer la facture aux ventes avant de les sauvegarder
        for(Sales sale : salesList) {
            //Associer à chaque vente à la facture
            sale.setInvoice(invoice);
        }

       // Sauvegarder les ventes AVANT de créer la facture
       repo.saveAll(salesList);
        /**Log pour chaque vente
        salesList.forEach(sale ->
                historyService.logInsert("ventes", sale));**/

       // Retourner la facture créée
       return invoice;
    }
    public byte[] generateInvoicePdf(SalesForm salesForm, Users user) throws IOException {
       try {
           ByteArrayOutputStream baos = new ByteArrayOutputStream();
           PdfWriter writer = new PdfWriter(baos);
           PdfDocument pdfDoc = new PdfDocument(writer);
           Document document = new Document(pdfDoc);

           // Récupération de la dernière facture
           Invoices invoice = invoicesRepository.findTopByOrderByIdInvoiceDesc();
           if (invoice == null) {
               throw new RuntimeException("Aucune facture trouvée");
           }

           // Récupérer l'année et le mois actuels
           YearMonth current = YearMonth.now();
           String formattedCurrent = current.format(DateTimeFormatter.ofPattern("yyyyMM"));

           // Débogage : Afficher les médicaments dans la console
           System.out.println("Nombre de médicaments dans la facture : " + salesForm.getItems().size());
           for (SalesItem item : salesForm.getItems()) {
               //System.out.println("Médicament : " + item.getDrugName() + ", Quantité : " + item.getQuantity());
              /** Drugs drug = repoDrugs.findById(item.getIdDrug())
                       .orElseThrow(()-> new RuntimeException("Médicaments non trouvé"));**/
           }



           // Titre
           document.add(new Paragraph("PHARAMACIE HUMANOPHAR")
                   .setTextAlignment(TextAlignment.CENTER)
                   .setFontSize(18)
                   .setBold());
           //INFORMATION SUR LA FACTURE
           document.add(new Paragraph("NIF : 4000020661"))
                           .setTextAlignment(TextAlignment.LEFT);
           document.add(new Paragraph("Téléphone : 79970620"))
                   .setTextAlignment(TextAlignment.LEFT);
           document.add(new Paragraph("Commune Mukaza"))
                   .setTextAlignment(TextAlignment.LEFT);
           document.add(new Paragraph("Adresse : Peuple Murundi"))
                   .setTextAlignment(TextAlignment.LEFT);
           document.add(new Paragraph("Date : " +LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                   .setTextAlignment(TextAlignment.LEFT));
           document.add(new Paragraph("Servi par : " + user.getNomUtilisateur()))
                   .setTextAlignment(TextAlignment.LEFT);
           document.add(new Paragraph("Facture N° : " + invoice.getIdInvoice() + "/" + formattedCurrent)
                   .setTextAlignment(TextAlignment.LEFT));
           document.add(new Paragraph(" "));// Espacement

           // En-tête des articles
           document.add(new Paragraph("DETAIL DES MÉDICAMENTS")
                   .setTextAlignment(TextAlignment.LEFT)
                   .setBold());

           // Tableau des médicaments
           Table table = new Table(UnitValue.createPercentArray(4)).useAllAvailableWidth();
           table.addHeaderCell("Médicament");
           table.addHeaderCell("Quantité");
           table.addHeaderCell("Prix Unitaire");
           table.addHeaderCell("Total");

           //Ajout des médicaments dans la facture
           for (SalesItem item : salesForm.getItems()) {
               table.addCell(item.getDrugName());
               table.addCell(String.valueOf(item.getQuantity()));
               table.addCell(String.format("%.2f Fbu", item.getUnitPrice()));
               table.addCell(String.format("%.2f Fbu", item.getTotalAmount()));
           }

           //Ajout di total général
           BigDecimal totalAmount = salesForm.getItems().stream()
                   .map(SalesItem::getTotalAmount)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
           table.addCell("Total :");
           table.addCell("");
           table.addCell("");
           table.addCell(String.format("%.2f Fbu", totalAmount));

           document.add(table);

           document.add(new Paragraph("MONTANT À PAYER: " + totalAmount + " Fbu")
                   .setTextAlignment(TextAlignment.RIGHT)
                   .setBold());
           // Informations supplémentaires
           document.add(new Paragraph(" ")); // Espacement
           document.add(new Paragraph("MERCI!!!!")
                   .setTextAlignment(TextAlignment.CENTER));

           document.close();
           return baos.toByteArray();
       } catch (Exception e) {
           System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
           throw e;
       }
    }
    public Sales get(Integer idSale) throws  SalesNotFoundException {
        Optional<Sales> result = repo.findById(idSale);
        if (result.isPresent()) {
            return result.get();
        }
        throw new SalesNotFoundException("Aucune vente avec l'identification :" +idSale);
    }
    public void deleteSale(Integer idSale) throws  SalesNotFoundException {
        /**Sales sale = repo.findById(Id)
                .orElseThrow(()-> new SalesNotFoundException("Aucune vente n'a été trouvée avec l'identification : " +Id));
        repo.deleteById(Id);
        historyService.logDelete("ventes", sale);**/
        Long count = repo.countByIdSale(idSale);
        if (count == null || count == 0) {
            throw new SalesNotFoundException("Aucune vente n'a été trouvée avec l'identification : " +idSale);
        }
        repo.deleteById(idSale);
    }
}










