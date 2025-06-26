package com.pharmacy.pharmacySystem;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/")
    public String showHomePage(Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER"))
                    ? "redirect:/home_user"
                    : "redirect:/home";
        }
        return "redirect:/loginPage";
    }
}
