package com.pharmacy.pharmacySystem;

import com.itextpdf.layout.properties.IListSymbolFactory;
import com.pharmacy.pharmacySystem.invoices.InvoicesRepository;
import com.pharmacy.pharmacySystem.purchases.PurchasesRepository;
import com.pharmacy.pharmacySystem.purchases.PurchasesService;
import com.pharmacy.pharmacySystem.sales.SalesRepository;
import com.pharmacy.pharmacySystem.sales.SalesService;
import com.pharmacy.pharmacySystem.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Controller
public class homeController {
    @Autowired private SalesService salesService;
    @Autowired private PurchasesService purchasesService;
    @Autowired private StockService stockService;
    @Autowired private SalesRepository salesRepo;
    @Autowired private PurchasesRepository purchasesRepo;
    @Autowired private InvoicesRepository invoicesRepo;

    @GetMapping("/home")
    public String homePage(Model model) {
        // Récupérer le total des ventes pour aujourd'hui
        BigDecimal todaySales = salesService.getTodaySalesTotal();
        // Récupérer le total des achats pour aujourd'hui
        BigDecimal todayPurchases = purchasesService.getTodayPurchasesTotal();
        //Récupérer le total de l'inventaire
        BigDecimal totalInventory = stockService.getTotalStockValue();

        //Statistiques des dernières 24h
        long nbInvoicesToday = invoicesRepo.countInvoicesToday();
        long nbSalesToday = salesRepo.countSalesToday();
        long nbPurchasesToday = purchasesRepo.countPurchasesToday();

        //Top Médicaments (les plus vendus)
        List<Object[]> topDrugs = salesRepo.findTopSellingDrugs(PageRequest.of(0,5));

        // Formater le montant
        String formattedSalesTotal = todaySales.setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
        String formattedPurchasesTotal = todayPurchases.setScale(2, RoundingMode.HALF_UP)
                        .toPlainString();
        String formattedInventoryTotal = totalInventory.setScale(2, RoundingMode.HALF_UP)
                        .toPlainString();
        model.addAttribute("todaySalesTotal", formattedSalesTotal);
        model.addAttribute("todayPurchasesTotal", formattedPurchasesTotal);
        model.addAttribute("totalInventory", formattedInventoryTotal);
        model.addAttribute("nbInvoices", nbInvoicesToday);
        model.addAttribute("nbSales", nbSalesToday);
        model.addAttribute("nbPurchases", nbPurchasesToday);
        model.addAttribute("topDrugs", topDrugs);
        return "home";
    }
}
