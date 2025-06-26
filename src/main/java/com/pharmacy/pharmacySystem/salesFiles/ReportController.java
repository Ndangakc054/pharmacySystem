package com.pharmacy.pharmacySystem.salesFiles;

import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.sales.SalesRepository;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired private ReportService reportService;
    @Autowired private SalesRepository salesRepo;
    @Autowired private UsersRepository usersRepo;

    @CrossOrigin(origins = "http://localhost:8082",
            allowedHeaders = "*",
            allowCredentials = "true",
            methods = {RequestMethod.POST}
    )
    @PostMapping("/daily")
    public ResponseEntity<byte[]> generateDailyReport(Authentication authentication) {
        System.out.println("===== DÉBUT GÉNÉRATION RAPPORT =====");

        try {
            // 1. Authentification
            System.out.println("[1/4] Authentication: " + authentication);
            String userName = authentication.getName();
            System.out.println("[2/4] Username: " + userName);

            // 2. Récupérer l'utilisateur connecté via son nom
            Users currentUser = usersRepo.findByNomUtilisateur(userName)
                    .orElseThrow(()-> new UsernameNotFoundException("Utilisateur non trouvé"));
            System.out.println("[3/4] User ID: " + currentUser.getId());

            // 3. Récupérer les ventes du jour
            LocalDate today = LocalDate.now();
            List<Sales> todaySales = salesRepo.findByUserIdAndDate(currentUser.getId(), today);
            System.out.println("[4/4] Nombre de ventes: " + todaySales.size());

            // 4. Générer et retourner le PDF
            byte[] pdfBytes = reportService.generateDailyReport(currentUser, todaySales);
            System.out.println("===== PDF GÉNÉRÉ AVEC SUCCÈS =====");

            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    /**.header("Content-Disposition", "inline; filename=rapport_journalier.pdf")**/
                    .body(pdfBytes);
        } catch (Exception e) {
            System.err.println("===== ERREUR CRITIQUE =====");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
