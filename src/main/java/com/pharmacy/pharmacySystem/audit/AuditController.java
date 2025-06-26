package com.pharmacy.pharmacySystem.audit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AuditController {
    @Autowired private ActionsHistoryRepository repo;
    @GetMapping("/history")
    public String showHistoryPage(Model model) {
        /**List<ActionsHistory> actions = repo.findAllByOrderByDateActionDesc();
        model.addAttribute("actions", actions);**/
        model.addAttribute("historiques",repo.findAllByOrderByDateActionDesc());
        return "history";
    }
    @GetMapping("/historyDetails/{id}")
    public String showHistoryDetails(@PathVariable("id") Integer id, Model model) {
        ActionsHistory log = repo.findById(id)
                        .orElseThrow(()-> new IllegalArgumentException("Invalid action id: " + id));
        model.addAttribute("log", log);
        return "historyDetails";
    }
}
