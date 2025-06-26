package com.pharmacy.pharmacySystem.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    @Autowired private UsersRepository repo;
    @Autowired private PasswordEncoder passwordEncoder;
    //@Autowired private ActionsHistoryService historyService;

    public UsersService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    public List<Users> listAll() {
        return  (List<Users>)  repo.findAll();
    }
    public void save(Users user) {
        boolean isNew = user.getId() == null;
        if (!user.getMotDePasse().startsWith("$2a$")){
            user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        }

        /**if (isNew) {
            Users saved = repo.save(user);
            historyService.logInsert("users", saved);
        } else {
            Users old = repo.findById(user.getId()).orElseThrow(null);
            Users saved = repo.save(user);
            historyService.logUpdate("users", old, saved);
        }**/
        repo.save(user);
    }
    public Users get(Integer Id) throws UsersNotFoundException {
        Optional<Users> result = repo.findById(Id);
        if(result.isPresent()){
            return result.get();
        }
        throw new UsersNotFoundException("Aucun utilisateur n'a été trouvé avec l'identification :" +Id);
    }
    public void delete(Integer Id) throws UsersNotFoundException {
        /**Users user = repo.findById(Id)
                .orElseThrow(()-> new UsersNotFoundException("Aucun utilisateur n'a été trouvé avec l'identifiant :"+Id));
        repo.deleteById(Id);
        historyService.logDelete("users", user);**/
        Long count = repo.countById(Id);
        if (count == null || count == 0) {
            throw new UsersNotFoundException("Aucun utilisateur n'a été trouvé avec l'identifiant :"+Id);
        }
        repo.deleteById(Id);
    }
    public void encodeExistingMotDePasse(){
        List<Users> users = listAll();
        for (Users user : users) {
            if (!user.getMotDePasse().startsWith("$2a$")) {
                user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
                repo.save(user);
            }
        }
    }
    public Optional<Users> findByUsername(String username) {
        return repo.findByNomUtilisateur(username);
    }
}
