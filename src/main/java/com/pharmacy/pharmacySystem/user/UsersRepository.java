package com.pharmacy.pharmacySystem.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UsersRepository extends CrudRepository<Users, Integer> {
    public Long countById(Integer id);
    Optional<Users> findById(Integer id);
    Optional<Users> findByNomUtilisateur(String username);
}