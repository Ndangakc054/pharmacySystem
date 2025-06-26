package com.pharmacy.pharmacySystem.user;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class UserDetailsServiceConfig {
    private final UsersRepository repo;

    public UserDetailsServiceConfig(UsersRepository repo) {
        this.repo = repo;
    }
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repo.findByNomUtilisateur(username)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getNomUtilisateur())
                        .password(user.getMotDePasse())
                        .roles(mapRole(user.getType()))
                        .build())
                .orElseThrow(()-> new UsernameNotFoundException("Utilisateur non trouvé :"+ username));
    }
    private String[] mapRole(String typeuser) {
        switch (typeuser.toUpperCase()) {
            case "ADMIN":
                return new String[]{"ADMIN"};
            case "USER":
                return new String[]{"USER"};
            default:
                throw new IllegalArgumentException("Type d'utilisateur inconu : "+typeuser);
        }
    }
}
