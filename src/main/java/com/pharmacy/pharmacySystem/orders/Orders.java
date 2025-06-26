package com.pharmacy.pharmacySystem.orders;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.purchaseFiles.PurchaseFiles;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.user.Users;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "purchase_drugs")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_out_of_stock")
    private Long idOrder;

    @ManyToOne
    @JoinColumn(name = "id_drug", nullable = false)
    private Drugs drug;

    @Column(name = "drug_name", nullable = false)
    private String nameDrug;

    @Column(name = "min_threshold", nullable = false)
    private Integer minimumThreshold;

    @Column(name = "date_out_of_stock")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOutOfStock;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private PurchaseFiles purchaseFile;

    public Long getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(Long idOrder) {
        this.idOrder = idOrder;
    }

    public Drugs getDrug() {
        return drug;
    }

    public void setDrug(Drugs drug) {
        this.drug = drug;
    }

    public String getNameDrug() {
        return nameDrug;
    }

    public void setNameDrug(String nameDrug) {
        this.nameDrug = nameDrug;
    }

    public Integer getMinimumThreshold() {
        return minimumThreshold;
    }

    public void setMinimumThreshold(Integer minimumThreshold) {
        this.minimumThreshold = minimumThreshold;
    }

    public LocalDate getDateOutOfStock() {
        return dateOutOfStock;
    }

    public void setDateOutOfStock(LocalDate dateOutOfStock) {
        this.dateOutOfStock = dateOutOfStock;
    }

    public PurchaseFiles getPurchaseFile() {
        return purchaseFile;
    }

    public void setPurchaseFile(PurchaseFiles purchaseFile) {
        this.purchaseFile = purchaseFile;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "idOrder=" + idOrder +
                ", drug=" + drug +
                ", nameDrug='" + nameDrug + '\'' +
                ", minimumThreshold=" + minimumThreshold +
                ", dateOutOfStock=" + dateOutOfStock +
                ", purchaseFile=" + purchaseFile +
                '}';
    }
}
