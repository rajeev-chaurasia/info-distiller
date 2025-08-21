package com.infodistiller.gateway;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    @Value("${app.api-key}")
    private String apiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Skip authentication for OPTIONS requests (CORS preflight)
        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Check API key
        String providedApiKey = request.getHeader("X-API-Key");
        
        if (apiKey.equals(providedApiKey)) {
            // Set simple authentication
            SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("api-user", null, Collections.emptyList())
            );
        }

        filterChain.doFilter(request, response);
    }
}
