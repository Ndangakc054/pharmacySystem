package com.pharmacy.pharmacySystem.settings;

import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class settingsController {
    @Autowired private UsersService usersService;
    @GetMapping("/settings")
    public String settingsPage() {
        return "settings";
    }
    @GetMapping("/settings/users")
    public String redirectToUsers(){
        return "redirect:/users";
    }
}