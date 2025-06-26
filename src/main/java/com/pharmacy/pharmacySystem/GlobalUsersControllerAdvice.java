package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalUsersControllerAdvice {
    @Autowired private UsersRepository repo;

    @ModelAttribute("loggedUser")
    public Users getCurrentUser(){
        // Retrieve the logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
        && !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            // Load user data from database
            return repo.findByNomUtilisateur(username).orElse(null);
        }
        return null;
    }
}
