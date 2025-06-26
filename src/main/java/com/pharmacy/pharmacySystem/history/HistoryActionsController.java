package com.pharmacy.pharmacySystem.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HistoryActionsController {
    @Autowired private HistoryActionsService service;
    @GetMapping("/historySql")
    public String showHistorySqlPage(Model model) {
        List<HistoryActions> historyList = service.listAll();
        model.addAttribute("historyList", historyList);
        return "historySql";
    }
}
