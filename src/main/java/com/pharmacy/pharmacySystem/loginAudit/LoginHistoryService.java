package com.pharmacy.pharmacySystem.loginAudit;

import com.pharmacy.pharmacySystem.user.Users;
import com.pharmacy.pharmacySystem.user.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class LoginHistoryService {

    private final LoginHistoryRepository repo;
    private final UsersRepository usersRepo;
    private final Map<String, Long> activeSessions = new ConcurrentHashMap<>();

    public LoginHistoryService(LoginHistoryRepository repo,
                               UsersRepository usersRepo) {
        this.repo = repo;
        this.usersRepo = usersRepo;
    }

    public LoginHistory recordLogin(Integer userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("Utilisateur non trouvé(e)"));

        LoginHistory history = new LoginHistory(user);
        LoginHistory savedHistory = repo.save(history);

        //Stocker dans la session
        activeSessions.put(generateSessionKey(userId), savedHistory.getIdLogin());
        return savedHistory;
    }

    @Transactional
    public void performLogout(Long historyId, Integer userId) {
        Optional<LoginHistory> historyOpt = repo.findById(historyId);
        if (historyOpt.isPresent()) {
            LoginHistory history = historyOpt.get();
            history.recordLogout();
        } else {
            repo.findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(userId)
                    .ifPresent(history -> {
                        history.recordLogout();
                    });
        }
    }

    public void recordLogout(Integer userId) {
        String sessionKey = generateSessionKey(userId);
        Long historyId = activeSessions.get(sessionKey);

        if (historyId != null) {
            Optional<LoginHistory> historyOpt = repo.findById(historyId);
            if (historyOpt.isPresent()) {
                LoginHistory history = historyOpt.get();
                history.recordLogout();
                repo.save(history);
            }
            activeSessions.remove(sessionKey);
        } else {
            // Gestion des sessions expirées ou non trouvées
            //logger.warn("Aucune session active trouvée pour l'utilisateur: {}", userId);

            // Optionnel : tenter de récupérer la dernière connexion non fermée
            repo.findTopByUserIdAndLogoutTimeIsNullOrderByLoginTimeDesc(userId)
                    .ifPresent(history -> {
                        history.recordLogout();
                        repo.save(history);
                    });
        }
    }
    /**public void recordLogout(Integer userId){
        String sessionKey = generateSessionKey(userId);
        Long historyId = activeSessions.get(sessionKey);

        if (historyId != null) {
            LoginHistory history = repo.findById(historyId)
                    .orElseThrow(()-> new EntityNotFoundException("Historique non trouvé ! "));

            history.recordLogout();
            repo.save(history);
            activeSessions.remove(sessionKey);
        } else {
            // Gérer le cas où la session n'est pas trouvée
            //logger.warn("Aucune session active trouvée pour l'utilisateur: {}", userId);
        }
    }**/

    private String generateSessionKey(Integer userId) {
        return "user_" + userId;
    }

    public List<LoginHistory> listAll() {
        return (List<LoginHistory>) repo.findAll();
    }
}
