package com.pharmacy.pharmacySystem;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeUserController {
    @GetMapping("/home_user")
    public String showHomeUserPage() {
        return "home_user";
    }
}
