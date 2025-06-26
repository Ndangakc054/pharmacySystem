package com.pharmacy.pharmacySystem.invoices;


import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.sales.SalesRepository;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InvoiceService {
    @Autowired private InvoicesRepository repo;
    @Autowired private  SalesRepository salesRepo;
    @Autowired private UsersRepository usersRepo;
    @Autowired private DrugsRepository drugsRepo;


    public List<Invoices> listAll() {
        return (List<Invoices>) repo.findAll();
    }
    public Invoices getInvoiceById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));
    }
    public void updateInvoice(Integer id, Invoices updateInvoice) {
        /**Invoices invoice = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        // Mettre à jour l'utilisateur
        Users user = usersRepo.findById(updateInvoice.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur no trouvé"));
        //Mettre à jour les informations de base

        invoice.setUser(user);
        invoice.setDateVente(updateInvoice.getDateVente());

        //Mettre à jour les ventes associées
        List<Sales> updatedSales = updateInvoice.getSales();
        List<Sales> existingSales = invoice.getSales();

        // Supprimer les ventes non présentes dans la mise à jour
        existingSales.removeIf(sale -> !updatedSales.contains(sale));

        // Ajouter ou mettre à jour les ventes

        for (Sales updatedSale : updatedSales) {
            // Vérifier que PrixUnitaire n'est pas null
            if (updatedSale.getPrixUnitaire() == null ) {
                throw new RuntimeException("PrixUnitaire est null pour la vente : " +updatedSale.getIdSales());
            }
            // Vérifier que  Quantity n'est pas null
            if (updatedSale.getQuantity() == null ) {
                throw new RuntimeException("Quantité est null pour la vente : " +updatedSale.getIdSales());
            }
            //Calculer le prix total automatiquement
            BigDecimal priwTotal = updatedSale.getPrixUnitaire().multiply(BigDecimal.valueOf(updatedSale.getQuantity()));
            updatedSale.setPrixTotal(priwTotal);
            if (updatedSale.getIdSales() == null) {
                updatedSale.setInvoice(invoice);
                existingSales.add(updatedSale);
            } else {
                Sales existingSale = existingSales.stream()
                        .filter(s -> s.getIdSales().equals(updatedSale.getIdSales()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Vente non trouvéé"));
                existingSale.setDrug(drugsRepo.findByIdDrug(updatedSale.getDrug().getNameDrug())
                        .orElseThrow(() -> new RuntimeException("Médicament non trouvé")));
                existingSale.setQuantity(updatedSale.getQuantity());
                existingSale.setPrixUnitaire(updatedSale.getPrixUnitaire());
                existingSale.setPrixTotal(updatedSale.getPrixTotal());
            }
        }**/
        /**for (Sales updatedSale : updatedSales) {
            Sales sale = salesRepo.findById(updatedSale.getId())
                    .orElseThrow(() -> new RuntimeException("Vente non trouvée"));
            sale.setDrug(drugsRepo.findByNameDrug(updatedSale.getDrug().getNameDrug())
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé")));
            sale.setQuantity(updatedSale.getQuantity());
            sale.setPrixUnitaire(updatedSale.getPrixUnitaire());
            sale.setPrixTotal(updatedSale.getPrixTotal());
        }**/
        //repo.save(invoice);
    }
    public void deleteInvoice(Integer Id) throws InvoicesNotFoundException {
        // Récupération complète de la facture avant suppression
        Invoices invoiceToDelete = repo.findById(Id)
                .orElseThrow(() -> new InvoicesNotFoundException("Aucune facture n'a été trouvée avec l'id : " + Id));
        // Suppression
        repo.deleteById(Id);

        //Log de l'action
        //historyService.logDelete("invoices", invoiceToDelete);
        Long count = repo.countByIdInvoice(Id);
        if (count == null || count == 0) {
            throw  new InvoicesNotFoundException("Aucune facture n'a été triuvée avec l'id : " +Id);
        }
        repo.deleteById(Id);
    }
}
