package com.pharmacy.pharmacySystem.config;


import com.pharmacy.pharmacySystem.loginAudit.LoginHistoryService;
import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Collection;
import java.util.Collections;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    //@Autowired private UsersRepository usersRepo;
    @Autowired private LoginHistoryService loginHistoryService;
    @Autowired private UsersRepository usersRepo;

    /**private final LoginHistoryService loginHistoryService;
    private final UsersRepository usersRepo;

    public SecurityConfig( LoginHistoryService loginHistoryService,
                          UsersRepository usersRepo) {
        this.loginHistoryService = loginHistoryService;
        this.usersRepo = usersRepo;
    }**/
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> auth
                        .requestMatchers("/loginPage", "/css/**","/js/**","/imgs/**").permitAll()
                        .requestMatchers("/home","/drugs", "/editDrug","/stock",
                                "/editDrugStock","/expiry","/sales","/recentSales","allSales","/purchases","all_purchases","/editPurchase",
                                "/orders","/suppliers","/editSupplier","/settings","/users","addUser","editUser",
                                "/invoicesPage","/history","/historyDetails/**","/loginHistory/**").hasRole("ADMIN")
                        .requestMatchers("home_user","/drugs_user","/stock_user","/expiry_user","/sales_user","/recentSales_user",
                                "/purchases_user","/orders_user","allSales_user","all_purchases_user","/suppliers_user/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET,"addDrug","/addDrugStock","/addPurchase","/addSupplier/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,"/addDrug","/addDrugStock","/addPurchase","/addSupplier/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET,"addDrug_user","/addDrugStock_user","addPurchase_user","addSupplier_user/**").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin( form -> form
                        .loginPage("/loginPage")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home",true)
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/loginPage?error=true")
                        .permitAll()
                )
                .logout(config -> config
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout","POST"))
                        .logoutSuccessUrl("/loginPage?logout=true")
                        .addLogoutHandler(logoutHandler())
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/loginPage")
                        .sessionFixation().migrateSession()
                )
                .userDetailsService(userDetailsService())
                .headers(headers -> headers
                        .cacheControl(cache -> cache.disable()) // Disable caching
                        .defaultsDisabled() // Désactive les en-têtes par défaut pour les renforcer
                        .frameOptions(frame -> frame.sameOrigin()) // Protection contre les attaques de type clickjacking
                        .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(3536000))  // HSTS
                        .contentSecurityPolicy(csp -> csp
                                .reportOnly()
                                .policyDirectives(
                                "default-src 'self'; " +
                                        "font-src 'self' https://fonts.googleapis.com; " +
                                        "script-src 'self' ; " +
                                        "style-src 'self' https://fonts.googleapis.com; " +
                                        "img-src 'self';"
                                )
                        ) // CSP
                )
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Gestionnaire de succès de login
        private AuthenticationSuccessHandler authenticationSuccessHandler() {
            return (request, response, authentication) -> {
            String username = authentication.getName();
            Users user = usersRepo.findByNomUtilisateur(username)
                    .orElseThrow(()-> new EntityNotFoundException("Utilisateur non trouvé"));
            loginHistoryService.recordLogin(user.getId());

            //Redirection selon le role
            Collection<? extends GrantedAuthority>  authorities = authentication.getAuthorities();
            String targetUrl = "/home"; //Par default admin

            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_USER")) {
                    targetUrl = "home_user";
                    break;
                }
            }
            response.sendRedirect(targetUrl);
        };
    }

    // Gestionnaire de logout
    private LogoutHandler logoutHandler() {
        return (request, response, authentication) -> {
            if (authentication != null) {
                try {
                    /**String username = authentication.getName();
                    Users user = usersRepo.findByNomUtilisateur(username)
                            .orElseThrow(()-> new EntityNotFoundException("Utilisateur non trouvé"));
                    loginHistoryService.recordLogout(user.getId());**/

                    // Stocker l'ID avant invalidation
                    Integer userId = usersRepo.findByNomUtilisateur(authentication.getName())
                            .map(Users::getId)
                            .orElse(null);
                    if (userId != null) {
                        loginHistoryService.recordLogout(userId);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'enregistrement de la déconnexion" + e.getMessage());
                }
            }
        };
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usersRepo.findByNomUtilisateur(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getNomUtilisateur(),
                        user.getMotDePasse(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getType().toUpperCase()))
                )).orElseThrow(()-> new UsernameNotFoundException("Utilisateur non trouvé"));
    }
}
