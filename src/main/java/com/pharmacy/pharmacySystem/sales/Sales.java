package com.pharmacy.pharmacySystem.sales;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pharmacy.pharmacySystem.invoices.Invoices;
import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.user.Users;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sales {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sales")
    private Integer idSale;

    @ManyToOne
    @JoinColumn(name = "id_drug", nullable = false)
    private Drugs drug;

    @Column(name = "drug_name", nullable = false, length = 100)
    private String nameDrug;

    @Column(name = "quantity", nullable = false, length = 11)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixUnitaire;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal prixTotal;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @Column(name = "date_sales")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime dateVente;
    @ManyToOne

    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoices invoice;

    //Getters et setters


    public Integer getIdSale() {
        return idSale;
    }

    public void setIdSale(Integer idSale) {
        this.idSale = idSale;
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

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public Invoices getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoices invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "Sales{" +
                "idSale=" + idSale +
                ", drug=" + drug +
                ", nameDrug='" + nameDrug + '\'' +
                ", quantity=" + quantity +
                ", prixUnitaire=" + prixUnitaire +
                ", prixTotal=" + prixTotal +
                ", user=" + user +
                ", userName='" + userName + '\'' +
                ", dateVente=" + dateVente +
                ", invoice=" + invoice +
                '}';
    }
}
