package com.pharmacy.pharmacySystem.audit;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.hibernate.mapping.Collection;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AuditService {
    private final ActionsHistoryRepository repo;
    private final ObjectMapper objectMapper;

    public AuditService(ActionsHistoryRepository repo, ObjectMapper objectMapper) {
        this.repo = repo;
        this.objectMapper = objectMapper;
    }

    //@Transactional
    public void logAction(String tableCible, String typeAction, Integer idEnregistrement
            , Object valuesBefore, Object valuesAfter) {
        // Validation obligatoire de l'ID
        /**if (idEnregistrement == null || idEnregistrement.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID d'enregistrement ne peut pas être null ou vide");
        }**/
            //Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null) ? auth.getName() : "SYSTEM";

            try {
                // Désinitialiser les proxies Hibernate
               valuesBefore = initializeAndUnproxy(valuesBefore);
                valuesAfter = initializeAndUnproxy(valuesAfter);
            /**if(valuesBefore != null) {
                Hibernate.initialize(valuesBefore);
                if (valuesBefore instanceof HibernateProxy) {
                    valuesBefore = ((HibernateProxy) valuesBefore).getHibernateLazyInitializer()
                            .getImplementation();
                }
            }
            if (valuesAfter != null) {
                Hibernate.initialize(valuesAfter);
                if (valuesAfter instanceof HibernateProxy) {
                    valuesAfter = ((HibernateProxy) valuesAfter).getHibernateLazyInitializer()
                            .getImplementation();
                }
            }**/
            ActionsHistory log = new ActionsHistory();
            log.setUtilisateur(username);
            log.setTableCible(tableCible);
            log.setTypeAction(typeAction);
            log.setIdEnregistrement(idEnregistrement);

            // Utilisation d'une méthode robuste de conversion
                if (valuesBefore != null) {
                    log.setValuesBefore(convertEntityToJson(valuesBefore));
                }
                if (valuesAfter != null) {
                    log.setValuesAfter(convertEntityToJson(valuesAfter));
                }
            // Convertir en Map filtrée
           /** if (valuesBefore != null) {
                Map<String, Object> filteredBefore = convertToFilteredMap(valuesBefore);
                log.setValuesBefore(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(filteredBefore));
            }
            // Utiliser directement l'ObjectMapper configuré
            if (valuesAfter != null) {
                Map<String, Object> filteredAfter = convertToFilteredMap(valuesAfter);
                log.setValuesAfter(objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(filteredAfter));
            }**/

            /**if (valuesBefore != null) {
                Map<String, Object> filteredBefore = convertToFilteredMap(valuesBefore);
                log.setValuesBefore(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filteredBefore));
            }
            if (valuesAfter != null) {
                Map<String, Object> filteredAfter = convertToFilteredMap(valuesAfter);
                log.setValuesAfter(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(filteredAfter));
            }**/

            /**if (valuesBefore != null) {
                log.setValuesBefore(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(valuesBefore));
            }
            if (valuesAfter != null) {
                log.setValuesAfter(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(valuesAfter));
            }**/
            repo.save(log);
        } catch (Exception e) {
                System.err.println("Erreur d'audit: " + e.getMessage());
                e.printStackTrace();
            //throw new RuntimeException("Erreur lors de la journalisation de l'action", e);
        }
    }
    private Object initializeAndUnproxy(Object entity) {
        if (entity == null) return null;

        try {
            Hibernate.initialize(entity);
            if (entity instanceof HibernateProxy) {
                return ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
            }
            return entity;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de l'entité: " + e.getMessage());
            return entity;
        }
    }

    private Map<String, Object> convertToFilteredMap(Object entity) {
        Map<String, Object> result = new LinkedHashMap<>();
        /**ObjectMapper simpleMapper = new ObjectMapper();
        simpleMapper.registerModule(new JavaTimeModule());**/

        try {

            // Convertir l'entité en Map brute
            Map<String, Object> rawMap = objectMapper.convertValue(entity, new TypeReference<Map<String, Object>>() {});

            // Filtrer les champs indésirables
            for (Map.Entry<String, Object> entry : rawMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // Exclure les champs techniques et les relations profondes
                if (key.equals("hibernateLazyInitializer") || key.equals("handler")) continue;
                // Exclure les champs transients (comme dateExpirationFormatted)
                if (key.equals("dateExpirationFormatted")) continue;
                //Gérer les relations
                if (value instanceof BaseEntity) {
                    result.put(key + "Id", ((BaseEntity) value).getId());
                } else if (value instanceof LocalDate) {
                    result.put(key, ((LocalDate) value).toString());
                }else if (!(value instanceof Collection) && !(value instanceof Map)) {
                    result.put(key, value);
                }
                /**if (!key.equals("hiberateLazyInitializer") &&
                 !key.equals("handler") &&
                 !(value instanceof Map) &&
                 !(value instanceof Collection)) {
                 // Simplifier les relations (ne garder que l'ID)
                 if(value instanceof BaseEntity) {
                 result.put(key + "Id", ((BaseEntity) value).getId());
                 } else {
                 result.put(key, value);
                 }
                 }**/
            }
            return result;
        } catch (Exception e) {
            System.err.println("Erreur de conversion: " + e.getMessage());
            return Collections.singletonMap("error", "Conversion failed");
        }
    }
    private String convertEntityToJson(Object entity) throws JsonProcessingException {
        // Création d'un ObjectMapper dédié avec configuration garantie
        ObjectMapper safeMapper = new ObjectMapper();
        safeMapper.registerModule(new JavaTimeModule());
        safeMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        safeMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        safeMapper.addMixIn(Object.class, IgnoreHibernatePropertiesMixIn.class);

        // Conversion directe en JSON sans passer par une Map intermédiaire
        return safeMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
    }

    // Mixin pour ignorer les propriétés Hibernate
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public abstract static class IgnoreHibernatePropertiesMixIn {}

}
