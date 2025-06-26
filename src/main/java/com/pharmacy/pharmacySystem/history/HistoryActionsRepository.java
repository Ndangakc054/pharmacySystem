package com.pharmacy.pharmacySystem.history;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HistoryActionsRepository extends CrudRepository<HistoryActions, Integer> {
    List<HistoryActions> findAllByOrderByDateActionDesc();
}
