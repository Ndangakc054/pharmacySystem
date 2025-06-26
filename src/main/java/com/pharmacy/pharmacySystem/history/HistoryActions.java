package com.pharmacy.pharmacySystem.history;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historique_actions")
public class HistoryActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_action")
    private Integer idAction;

    @Column(name = "utilisateur", nullable = false, length = 50)
    private String utilisateur;

    @Column(name = "table_cible", nullable = false, length = 50)
    private String tableCible;

    @Column(name = "type_action", nullable = false, length = 50)
    private String typeAction; // "INSERT", "UPDATE", "DELETE"

    @Column(name = "id_enregistrement", nullable = false, length = 255)
    private String idEnregistrement;

    @Column(name = "valeurs_avant", columnDefinition = "TEXT")
    private String valuesBefore;

    @Column(name = "valeurs_après", columnDefinition = "TEXT")
    private String valuesAfter;

    @CreationTimestamp
    @Column(name = "date_action", nullable = false, updatable = false)
    private LocalDateTime dateAction;

    //Construteurs

    public HistoryActions() {

    }

    //Getters et setters


    public Integer getIdAction() {
        return idAction;
    }

    public void setIdAction(Integer idAction) {
        this.idAction = idAction;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    public String getTableCible() {
        return tableCible;
    }

    public void setTableCible(String tableCible) {
        this.tableCible = tableCible;
    }

    public String getTypeAction() {
        return typeAction;
    }

    public void setTypeAction(String typeAction) {
        this.typeAction = typeAction;
    }

    public String getIdEnregistrement() {
        return idEnregistrement;
    }

    public void setIdEnregistrement(String idEnregistrement) {
        this.idEnregistrement = idEnregistrement;
    }

    public String getValuesBefore() {
        return valuesBefore;
    }

    public void setValuesBefore(String valuesBefore) {
        this.valuesBefore = valuesBefore;
    }

    public String getValuesAfter() {
        return valuesAfter;
    }

    public void setValuesAfter(String valuesAfter) {
        this.valuesAfter = valuesAfter;
    }

    public LocalDateTime getDateAction() {
        return dateAction;
    }

    public void setDateAction(LocalDateTime dateAction) {
        this.dateAction = dateAction;
    }
}
