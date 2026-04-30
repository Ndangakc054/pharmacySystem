package com.pharmacy.pharmacySystem.orders;

import com.pharmacy.pharmacySystem.drugs.Drugs;
import com.pharmacy.pharmacySystem.drugs.DrugsRepository;
import com.pharmacy.pharmacySystem.stock.Stock;
import com.pharmacy.pharmacySystem.stock.StockRepository;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OrdersService {
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private UsersRepository usersRepo;
    @Autowired private DrugsRepository drugsRepo;
    @Autowired private StockRepository stockRepo;


    public List<Stock> refreshOrders() {
        String sqlQuery = "SELECT * FROM inventory ORDER BY quantity ASC";
        return jdbcTemplate.query(sqlQuery,  (rs, rowNum) -> {
            Stock stock = new Stock();
            stock.setIdStock(rs.getInt("id_inventory"));
            Drugs drug = new Drugs();
            drug.setNameDrug(rs.getString("drug_name"));
            stock.setDrug(drug);
            stock.setQuantity(rs.getInt("quantity"));
            return stock;
        });
    }

}
