package com.pharmacy.pharmacySystem.invoices;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.user.Users;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "invoices")
public class Invoices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_invoice")
    private Integer idInvoice;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "date_sale")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateVente;

    @Lob // Indique que c'est un BLOB
    @Column(name = "pdf_invoice", columnDefinition = "LONGBLOB")
    private byte[] pdfInvoice;

    @Column(name = "invoice_name", nullable = false, length = 255)
    private String invoiceName;

    //Relation 1:N avec Sales
    @JsonIgnore
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<Sales> sales;

    public Integer getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Integer idInvoice) {
        this.idInvoice = idInvoice;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getDateVente() {
        return dateVente;
    }

    public void setDateVente(LocalDateTime dateVente) {
        this.dateVente = dateVente;
    }

    public byte[] getPdfInvoice() {
        return pdfInvoice;
    }

    public void setPdfInvoice(byte[] pdfInvoice) {
        this.pdfInvoice = pdfInvoice;
    }

    public List<Sales> getSales() {
        return sales;
    }

    public void setSales(List<Sales> sales) {
        this.sales = sales;
    }

    public String getInvoiceName() {
        return invoiceName;
    }

    public void setInvoiceName(String invoiceName) {
        this.invoiceName = invoiceName;
    }
}
