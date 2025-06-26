package com.pharmacy.pharmacySystem.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.json.JsonWriteFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.lang.reflect.Method;
import java.time.LocalDateTime;


/**@Service
public class ActionsHistoryService {
    @Autowired private ActionsHistoryRepository repo;

    private static final Logger logger = LoggerFactory.getLogger(ActionsHistoryService.class);

    public void save(ActionsHistory action) {
        repo.save(action);
    }
    public void logInsert(String tableName, Object entity) {
        ActionsHistory action = new ActionsHistory();
        action.setTypeAction("INSERT");
        action.setTableCible(tableName);
        action.setUtilisateur(getConnectedUser());
        action.setDateAction(LocalDateTime.now());
        action.setValuesAfter(convertToJson(entity));
        action.setIdEnregistrement(getId(entity));
        repo.save(action);
    }

    public void logUpdate(String tableName, Object oldEntity, Object newEntity ) {
        ActionsHistory action = new ActionsHistory();
        action.setTypeAction("UPDATE");
        action.setTableCible(tableName);
        action.setUtilisateur(getConnectedUser());
        action.setDateAction(LocalDateTime.now());
        action.setValuesBefore(convertToJson(oldEntity));
        action.setValuesAfter(convertToJson(newEntity));
        action.setIdEnregistrement(getId(oldEntity));

        repo.save(action);
    }

    public void logDelete(String tableName, Object entity) {
        ActionsHistory action = new ActionsHistory();
        action.setTypeAction("DELETE");
        action.setTableCible(tableName);
        action.setUtilisateur(getConnectedUser());
        action.setDateAction(LocalDateTime.now());
        action.setValuesBefore(convertToJson(entity));
        action.setIdEnregistrement(getId(entity));
        repo.save(action);
    }
    private String getConnectedUser() {
        // Récupérer l'utilisateur connecté
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
    private String convertToJson(Object entity) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Pour gérer les LocalDate/LocalDateTime
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);  // Exclure les champs null
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // Configuration pour éviter l'échappement HTML
        mapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true); // Échapper les caractères non-ASCII
        mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true); // Garder les guillemets pour les noms
        //mapper.getFactory().configure(JsonWriteFeature.ESCAPE_HTML_CHARS.mappedFeature(), false);// Ne pas échapper <, >, &, etc.
        mapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            logger.error("Erreur de sérialisation pour l'entité " + entity.getClass(), e);
            return "{\"error\": \"Failed to serialize entity\"}";
        }
        /**try {
            return new ObjectMapper().writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            return "{}";
        }**/
    //}
    /**private Integer getId(Object entity) {
        try {
            Method getId = entity.getClass().getMethod("getId");
            return (Integer) getId.invoke(entity);
        } catch (Exception e) {
            return null;
        }
        /**try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            return (Integer) idField.get(entity);
        } catch (Exception e) {
            return null;
        }**/
   // }
//}
