package com.pharmacy.pharmacySystem.suppliers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pharmacy.pharmacySystem.audit.BaseEntity;
import com.pharmacy.pharmacySystem.purchases.Purchases;
import jakarta.persistence.*;

import java.math.BigInteger;
import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Suppliers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_supplier")
    private Integer idSupplier;

    @Column(name = "name", nullable = false, unique = true, length = 255)
    private String nomFournisseur;

    @Column(name = "nif", nullable = false, length = 20)
    private BigInteger nif;

    @Column(name = "adresse", nullable = false, length = 100)
    private String adresse;

    @Column(name = "email", nullable = false, length = 255)
    private  String email;

    //Relation 1:N with Purchases
    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Purchases> purchases;

    //Getters et setters

    public Integer getIdSupplier() {
        return idSupplier;
    }

    public void setIdSupplier(Integer idSupplier) {
        this.idSupplier = idSupplier;
    }

    public String getNomFournisseur() {
        return nomFournisseur;
    }

    public void setNomFournisseur(String nomFournisseur) {
        this.nomFournisseur = nomFournisseur;
    }

    public BigInteger getNif() {
        return nif;
    }

    public void setNif(BigInteger nif) {
        this.nif = nif;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Suppliers{" +
                "idSupplier=" + idSupplier +
                ", nomFournisseur='" + nomFournisseur + '\'' +
                ", nif=" + nif +
                ", adresse='" + adresse + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
