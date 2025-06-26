package com.pharmacy.pharmacySystem.loginAudit;

import com.pharmacy.pharmacySystem.user.Users;
import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_login")
    private Long idLogin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user", nullable = false)
    private Users user;

    @Column(name = "name_user", nullable = false)
    private String nameUser;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "connection_duration")
    private Long connectionDuration;

    // Constructeur vide requis par JPA/Hibernate
    public LoginHistory() {

    }

    //CONSTRUCTEURS
    public LoginHistory(Users user) {
        this.user = user;
        this.nameUser = user.getNomUtilisateur();
        this.loginTime = LocalDateTime.now();
        this.logoutTime = null;
        this.connectionDuration = null;
    }

    //METHODE POUR ENREGISTRER LA CONNEXION
    public void recordLogout() {
        this.logoutTime = LocalDateTime.now();
        if (this.loginTime != null && this.logoutTime != null) {
            this.connectionDuration = Duration.between(loginTime, logoutTime).getSeconds();
        } else {
            this.connectionDuration = 0L;
        }
    }

    //Getters et setters

    public Long getIdLogin() {
        return idLogin;
    }

    public void setIdLogin(Long idLogin) {
        this.idLogin = idLogin;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public String getNameUSer() {
        return nameUser;
    }

    public void setNameUSer(String nameUSer) {
        this.nameUser = nameUSer;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public Long getConnectionDuration() {
        return connectionDuration;
    }

    public void setConnectionDuration(Long connectionDuration) {
        this.connectionDuration = connectionDuration;
    }
}
