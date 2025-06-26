package com.pharmacy.pharmacySystem.audit;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ActionsHistoryRepository extends CrudRepository<ActionsHistory, Integer> {
    List<ActionsHistory> findAllByOrderByDateActionDesc();
}
