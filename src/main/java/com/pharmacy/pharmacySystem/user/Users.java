package com.pharmacy.pharmacySystem.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.pharmacySystem.invoices.Invoices;
import com.pharmacy.pharmacySystem.sales.Sales;
import com.pharmacy.pharmacySystem.salesFiles.SalesFiles;
import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id;
    @Column(name = "user_name", nullable = false, unique = true, length = 20)
    private String nomUtilisateur;
    @Column(name = "password", nullable = false, length = 255)
    private String motDePasse;
    @Column(name = "type_user", nullable = false, length = 20)
    private  String type;


    //Relation 1:N with Sales
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Sales> sales;


    //Relation 1:N with Invoices
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Invoices> invoices;

    //Relation 1:N with PurchaseFile
    /**@JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<PurchaseFiles> purchaseFiles;**/

   //Relation 1:N with Orders
   /** @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Orders> orders;**/

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomUtilisateur() {
        return nomUtilisateur;
    }

    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                ", motDePasse='" + motDePasse + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
