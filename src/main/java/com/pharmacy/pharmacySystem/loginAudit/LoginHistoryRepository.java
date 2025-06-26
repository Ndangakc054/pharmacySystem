package com.pharmacy.pharmacySystem.loginAudit;

import com.pharmacy.pharmacySystem.sales.Sales;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoginHistoryRepository extends CrudRepository<LoginHistory, Long> {
    //Optional<LoginHistory> findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(Integer userId);

    // Déclarez correctement la relation avec l'utilisateur
    /**@Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.logoutTime IS NULL ORDER BY lh.loginTime DESC")
    Optional<LoginHistory> findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(@Param("userId") Integer userId);**/

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId AND lh.logoutTime IS NULL ORDER BY lh.loginTime DESC")
    Optional<LoginHistory> findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(@Param("userId") Integer userId);

    //Trier les ventes par date de validation descendant
    @Query("SELECT s FROM LoginHistory s ORDER BY s.idLogin DESC")
    List<LoginHistory> findAllLoginHistoryByIdDesc();
}
