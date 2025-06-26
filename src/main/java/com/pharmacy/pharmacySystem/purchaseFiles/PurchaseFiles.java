package com.pharmacy.pharmacySystem.purchaseFiles;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.pharmacySystem.orders.Orders;
import com.pharmacy.pharmacySystem.user.Users;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "purchase_files")
public class PurchaseFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_file")
    private Integer idFile;
    @Column(name = "date_purchase")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOrders;

    @Lob
    @Column(name = "pdf_file", columnDefinition = "LONGBLOB")
    private byte[] pdfOrders;

    //Relation 1:N avec orders
    @JsonIgnore
    @OneToMany(mappedBy = "purchaseFile", cascade = CascadeType.ALL)
    private List<Orders> orders;

    public Integer getIdFile() {
        return idFile;
    }

    public void setIdFile(Integer idFile) {
        this.idFile = idFile;
    }

    /**public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }**/

    public LocalDate getDateOrders() {
        return dateOrders;
    }

    public void setDateOrders(LocalDate dateOrders) {
        this.dateOrders = dateOrders;
    }

    public byte[] getPdfOrders() {
        return pdfOrders;
    }

    public void setPdfOrders(byte[] pdfOrders) {
        this.pdfOrders = pdfOrders;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }
}
