package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {
    @Autowired private UsersService service;

    @ModelAttribute("loggedUser")
    public Users addLoggedUserToModel(Principal principal){
        if (principal != null) {
            return service.findByUsername(principal.getName()).orElse(null);
        }
        return null;
    }
}
