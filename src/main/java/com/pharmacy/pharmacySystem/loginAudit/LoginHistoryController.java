package com.pharmacy.pharmacySystem.loginAudit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginHistoryController {
    /**@Autowired private LoginHistoryRepository repo;
    @Autowired private DurationUtils durationUtils;**/

    private final LoginHistoryRepository repo;
    private final DurationUtils durationUtils;

    public LoginHistoryController(LoginHistoryRepository repo,
                                  DurationUtils durationUtils) {
        this.repo = repo;
        this.durationUtils = durationUtils;
    }

    @GetMapping("/loginHistory")
    public String showHistoryLoginPage(Model model) {
        model.addAttribute("historyList", repo.findAllLoginHistoryByIdDesc());
        model.addAttribute("utils", durationUtils);
        return "loginHistory";
    }
}
