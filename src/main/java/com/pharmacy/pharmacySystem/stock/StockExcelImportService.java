package com.pharmacy.pharmacySystem.stock;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Character.getNumericValue;

@Service
public class StockExcelImportService {

    @Autowired private StockRepository stockRepo;
    @Autowired private DrugsRepository drugsRepo;

    public void save(MultipartFile file) {
        try {
            List<Stock> stocks = excelToStocks(file.getInputStream());
            stockRepo.saveAll(stocks);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'importation du fichier Excel: " + e.getMessage());
        }

    }

    private List<Stock> excelToStocks(InputStream is) {
        try (Workbook workbook = new XSSFWorkbook(is)){
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<Stock> stocks = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()){
                Row currentRow = rows.next();

                // Sauter l'entête du fichier Excel
                if (rowNumber == 0){
                    rowNumber++;
                    continue;
                }

                // Vérifier si la ligne est vide (évite les erreurs sur les lignes fantômes en bas du fichier)
                if (currentRow.getCell(0) == null) break;

                Stock stock = new Stock();

                /**
                // Colonne 0: ID_DRUG
                int idDrug = (int) currentRow.getCell(0).getNumericCellValue();
                Drugs drug = drugsRepo.findById(idDrug).orElseThrow(() -> new RuntimeException("Médicament non trouvé ID: " + idDrug));
                stock.setDrug(drug);

                // Colonne 1: QUANTITY
                stock.setQuantity((int) currentRow.getCell(1).getNumericCellValue());

                // Colonne 2: EXP_DATE (Date d'expiration)
                stock.setDateExpiration(currentRow.getCell(2).getLocalDateTimeCellValue().toLocalDate());

                // Colonne 3: UNIT_PRICE
                stock.setPrixUnitaire(BigDecimal.valueOf(currentRow.getCell(3).getNumericCellValue()));

                // Colonne 4: ENTRY_DATE (Date d'entrée)
                stock.setDateEntree(currentRow.getCell(4).getLocalDateTimeCellValue().toLocalDate());
                 **/
                /**
                // Colonne 0: ID_DRUG (Sécurisé)
                int idDrug = (int) getNumericValue(currentRow.getCell(0));
                Drugs drug = drugsRepo.findById(idDrug)
                        .orElseThrow(() -> new RuntimeException("Ligne " + (currentRow.getRowNum()+1) + " : Médicament non trouvé ID: " + idDrug));
                stock.setDrug(drug);**/

                // --- COLONNE 0 : NOM DU MÉDICAMENT ---
                String drugNameExcel = currentRow.getCell(0).getStringCellValue().trim();

                // On cherche le médicament en BD par son nom
                // ATTENTION : Tu dois avoir une méthode findByNameDrug dans ton DrugsRepository
                Drugs drug = drugsRepo.findFirstByNameDrug(drugNameExcel);

                if (drug == null) {
                    throw new RuntimeException("Erreur Ligne " + (currentRow.getRowNum() + 1) +
                            " : Le médicament '" + drugNameExcel + "' n'existe pas dans la base de données.");
                }
                stock.setDrug(drug);

                /// --- COLONNE 1 : QUANTITÉ ---
                stock.setQuantity((int) getNumericValue(currentRow.getCell(1)));

                // --- COLONNE 2 : DATE D'EXPIRATION ---
                stock.setDateExpiration(currentRow.getCell(2).getLocalDateTimeCellValue().toLocalDate());

                // --- COLONNE 3 : PRIX UNITAIRE ---
                stock.setPrixUnitaire(BigDecimal.valueOf(getNumericValue(currentRow.getCell(3))));

                // --- COLONNE 4 : PRIX TOTAL (Ignoré car VIRTUAL GENERATED dans ta BD) ---
                // On ne fait rien, la BD le calculera toute seule.

                // --- COLONNE 5 : DATE D'ENTRÉE ---
                stock.setDateEntree(currentRow.getCell(5).getLocalDateTimeCellValue().toLocalDate());

                stocks.add(stock);
            }
            return stocks;
        } catch (IOException e) {
            throw new RuntimeException("Échec de la lecture du fichier Excel: " + e.getMessage());
        }
    }

    // MÉTHODE MAGIQUE : Elle lit le nombre, peu importe le format de la cellule Excel
    private double getNumericValue(Cell cell) {
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) return cell.getNumericCellValue();
        if (cell.getCellType() == CellType.STRING) {
            try { return Double.parseDouble(cell.getStringCellValue().trim()); } catch (Exception e) { return 0; }
        }
        return 0;
    }

}
