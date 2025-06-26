package com.pharmacy.pharmacySystem;

import com.pharmacy.pharmacySystem.user.Users;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {
    @GetMapping("/loginPage")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model, RedirectAttributes redirectAttributes) {
        if (error != null) {
                redirectAttributes.addFlashAttribute("status","error");
                redirectAttributes.addFlashAttribute("message","Nom d'utilisateur ou mot de passe incorrect");
        }
        if (logout != null) {
            redirectAttributes.addFlashAttribute("status","success");
            redirectAttributes.addFlashAttribute("message","Vous avez été déconnecté avec succès");
            return "redirect:/loginPage";
        }
        return "loginPage";
    }
    /**@GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return "redirect:/loginPage";
    }**/
}

