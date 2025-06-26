package com.pharmacy.pharmacySystem.purchases;

import com.pharmacy.pharmacySystem.audit.BaseEntity;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.suppliers.Suppliers;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchases")
public class Purchases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_purchase")
    private Integer idPurchase;

    @ManyToOne
    @JoinColumn(name = "id_drug", nullable = false)
    private Drugs drug;

    @Column(name = "drug_name", nullable = false, length = 100)
    private String nameDrug;

    @ManyToOne
    @JoinColumn(name = "id_supplier", nullable = false)
    private Suppliers supplier;

    @Column(name = "supplier_name", nullable = false, length = 255)
    private String nameSupplier;

    @Column(name = "quantity", nullable = false, length = 11)
    private  Integer quantity;

    @Column(name = "unit_purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixAchatUnitaire;

    @Column(name = "total_purchase_price", insertable = false,updatable = false, precision = 10, scale = 2)
    private BigDecimal prixAchatTotal;

    @Column(name = "date_purchase")
    private LocalDate dateAchat;
    @Column(name = "exp_date")
    private  LocalDate dateExpiration;


    //Champs transients pour le formatage des dates
    @Transient
    private String dateAchatFormatted = "";
    @Transient
    private String dateExpirationFormatted = "";

    public String getDateAchatFormatted() {
        return dateAchatFormatted;
    }

    public void setDateAchatFormatted(String dateAchatFormatted) {
        this.dateAchatFormatted = dateAchatFormatted;
    }

    public String getDateExpirationFormatted() {
        return dateExpirationFormatted;
    }

    public void setDateExpirationFormatted(String dateExpirationFormatted) {
        this.dateExpirationFormatted = dateExpirationFormatted;
    }

    //Getters et setters


    public Integer getIdPurchase() {
        return idPurchase;
    }

    public void setIdPurchase(Integer idPurchase) {
        this.idPurchase = idPurchase;
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

    public Suppliers getSupplier() {
        return supplier;
    }

    public void setSupplier(Suppliers supplier) {
        this.supplier = supplier;
    }

    public String getNameSupplier() {
        return nameSupplier;
    }

    public void setNameSupplier(String nameSupplier) {
        this.nameSupplier = nameSupplier;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrixAchatUnitaire() {
        return prixAchatUnitaire;
    }

    public void setPrixAchatUnitaire(BigDecimal prixAchatUnitaire) {
        this.prixAchatUnitaire = prixAchatUnitaire;
    }

    public BigDecimal getPrixAchatTotal() {
        return prixAchatTotal;
    }

    public void setPrixAchatTotal(BigDecimal prixAchatTotal) {
        this.prixAchatTotal = prixAchatTotal;
    }

    public LocalDate getDateAchat() {
        return dateAchat;
    }

    public void setDateAchat(LocalDate dateAchat) {
        this.dateAchat = dateAchat;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public String toString() {
        return "Purchases{" +
                "idPurchase=" + idPurchase +
                ", drug=" + drug +
                ", nameDrug='" + nameDrug + '\'' +
                ", supplier=" + supplier +
                ", nameSupplier='" + nameSupplier + '\'' +
                ", quantity=" + quantity +
                ", prixAchatUnitaire=" + prixAchatUnitaire +
                ", prixAchatTotal=" + prixAchatTotal +
                ", dateAchat=" + dateAchat +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}
