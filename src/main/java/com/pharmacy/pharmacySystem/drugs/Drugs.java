package com.pharmacy.pharmacySystem.drugs;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.pharmacySystem.audit.BaseEntity;
import com.pharmacy.pharmacySystem.orders.Orders;
import com.pharmacy.pharmacySystem.purchases.Purchases;
import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.stock.Stock;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "drugs")
public class Drugs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_drug")
    private Integer idDrug;
    @Column(name = "name_drug", nullable = false, unique = true, length = 255)
    private String nameDrug;
    @Column(name = "category", nullable = false, length = 50)
    private String categorie;
    @Column(name = "unit_of_meas", nullable = false, length = 20)
    private String uniteDeMesure;
    @Column(name = "quantity_per_unit", nullable = false)
    private Integer quantiteParUnite;
    @Column(name = "unit_sales_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;
    @Column(name = "date_exp")
    private LocalDate dateExpiration;
    //Relation 1:N with Stock
    @JsonIgnore
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Stock> stocks;
    //Relation with Purchases
    @JsonIgnore
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Purchases> purchases;
    //Relation 1:N with Sales
    @JsonIgnore
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sales> sales;
    //Relation 1:N with Orders
    @JsonIgnore
    @OneToMany(mappedBy = "drug", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Orders> orders;
    //CONSTUCTEURS

    public Drugs() {}
    public Drugs(Integer idDrug,String nameDrug, String categorie, String uniteDeMesure, Integer quantiteParUnite,
                 BigDecimal prixUnitaire, LocalDate dateExpiration){
        this.idDrug = idDrug;
        this.nameDrug = nameDrug;
        this.categorie = categorie;
        this.uniteDeMesure = uniteDeMesure;
        this.quantiteParUnite = quantiteParUnite;
        this.prixUnitaire = prixUnitaire;
        this.dateExpiration = dateExpiration;
    }

    // Champs transients pour le formatage des dates
    @Transient
    private String dateExpirationFormatted = "";

    public String getDateExpirationFormatted() {
        return dateExpirationFormatted;
    }

    public void setDateExpirationFormatted(String dateExpirationFormatted) {
        this.dateExpirationFormatted = dateExpirationFormatted;
    }
    //Getters & Setters

    public Integer getIdDrug() {
        return idDrug;
    }

    public void setIdDrug(Integer idDrug) {
        this.idDrug = idDrug;
    }

    public String getNameDrug() {
        return nameDrug;
    }

    public void setNameDrug(String nameDrug) {
        this.nameDrug = nameDrug;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getUniteDeMesure() {
        return uniteDeMesure;
    }

    public void setUniteDeMesure(String uniteDeMesure) {
        this.uniteDeMesure = uniteDeMesure;
    }

    public Integer getQuantiteParUnite() {
        return quantiteParUnite;
    }

    public void setQuantiteParUnite(Integer quantiteParUnite) {
        this.quantiteParUnite = quantiteParUnite;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    @Override
    public String toString() {
        return "Drugs{" +
                "idDrug=" + idDrug +
                ", nameDrug='" + nameDrug + '\'' +
                ", categorie='" + categorie + '\'' +
                ", uniteDeMesure='" + uniteDeMesure + '\'' +
                ", quantiteParUnite=" + quantiteParUnite +
                ", prixUnitaire=" + prixUnitaire +
                ", dateExpiration=" + dateExpiration +
                '}';
    }
}
