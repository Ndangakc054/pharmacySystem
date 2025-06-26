package com.pharmacy.pharmacySystem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CacheControlFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String uri = request.getRequestURI();
        if (!uri.startsWith("/css/") && !uri.startsWith("/js/") && !uri.startsWith("/imgs")) {
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); //HTTP 1.1
            response.setHeader("Pragma", "no-cache"); //HTTP 1.0
            response.setHeader("Expires","0");
            response.setHeader("Cache-Control","private, no-store, no-cache, must-revalidate, max-age=0"); //Extensions
        }
        filterChain.doFilter(request, response);
    }
}
