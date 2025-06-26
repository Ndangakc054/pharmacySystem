package com.pharmacy.pharmacySystem.loginAudit;

import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DurationUtils {
    public String formatDuration(Long seconds) {
        if (seconds == null ){
            return "NULL";
        }
        if (seconds == 0) {
            return "0 seconde";
        }

        long heures = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60;
        long secondesRestantes = seconds % 60;

        StringBuilder stringBuilder = new StringBuilder();

        if (heures > 0 ) {
            stringBuilder.append(heures).append(heures == 1 ? " heure " : " heures ");
        }
        if (minutes > 0) {
            stringBuilder.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
        }
        if (secondesRestantes > 0) {
            stringBuilder.append(secondesRestantes).append(secondesRestantes == 1 ? " seconde " : " secondes ");
        }
        return stringBuilder.toString().trim();
    }
}
