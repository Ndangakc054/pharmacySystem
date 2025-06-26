package com.pharmacy.pharmacySystem.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionIdListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionListener implements HttpSessionIdListener {
    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        String newSessionId = event.getSession().getId();
        System.out.println("Session ID changé : Ancien ID = " + oldSessionId
                + ", Nouveau ID = " + newSessionId);
    }
}
