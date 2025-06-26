package com.pharmacy.pharmacySystem.stock;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.pharmacy.pharmacySystem.audit.BaseEntity;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import jakarta.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "inventory")
public class Stock implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventory")
    private Integer idStock;

    @ManyToOne
    @JoinColumn(name = "id_drug", nullable = false)
    @JsonManagedReference
    private Drugs drug;

    @Column(name = "drug_name", insertable = false, updatable = false)
    private String nameDrug;

    @Column(name = "quantity", nullable = false, length = 11)
    private Integer quantity;

    @Column(name = "exp_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateExpiration;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "unit_price_total", insertable = false, updatable = false, precision = 10, scale = 2)
    private  BigDecimal prixTotal;

    @Column(name = "entry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEntree;

    //Relation 1:N with orders
   /**@JsonIgnore
    @OneToMany(mappedBy = "stock",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Orders> orders;**/

    // Champs transients pour le formatage des dates
    @Transient
    private String dateExpirationFormatted = "";
    @Transient
    private String dateEntreeFormatted = "";

    public String getDateExpirationFormatted() {
        return dateExpirationFormatted;
    }

    public void setDateExpirationFormatted(String dateExpirationFormatted) {
        this.dateExpirationFormatted = dateExpirationFormatted;
    }

    public String getDateEntreeFormatted() {
        return dateEntreeFormatted;
    }

    public void setDateEntreeFormatted(String dateEntreeFormatted) {
        this.dateEntreeFormatted = dateEntreeFormatted;
    }

    //Getters et setters


    public Integer getIdStock() {
        return idStock;
    }

    public void setIdStock(Integer idStock) {
        this.idStock = idStock;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public BigDecimal getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(BigDecimal prixTotal) {
        this.prixTotal = prixTotal;
    }

    public LocalDate getDateEntree() {
        return dateEntree;
    }

    public void setDateEntree(LocalDate dateEntree) {
        this.dateEntree = dateEntree;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "idStock=" + idStock +
                ", drug=" + drug +
                ", nameDrug='" + nameDrug + '\'' +
                ", quantity=" + quantity +
                ", dateExpiration=" + dateExpiration +
                ", prixUnitaire=" + prixUnitaire +
                ", prixTotal=" + prixTotal +
                ", dateEntree=" + dateEntree +
                '}';
    }
}
