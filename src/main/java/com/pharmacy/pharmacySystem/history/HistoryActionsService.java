package com.pharmacy.pharmacySystem.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistoryActionsService {
    @Autowired private HistoryActionsRepository repo;

    public List<HistoryActions> listAll() {
        return (List<HistoryActions>) repo.findAllByOrderByDateActionDesc();
    }
}
