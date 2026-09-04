package com.example.walletproject.auth.config;

import com.example.walletproject.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// Runs once per incoming request, before it reaches any controller.
// Reads the Authorization header, validates the JWT, and — if valid —
// tells Spring Security "this request is authenticated as this user."
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer " prefix

        try {
            String username = jwtService.extractUsername(token);

            // Only set authentication if not already set (avoids redundant work),
            // and only if the token is actually still valid (signature + expiration).
            if (SecurityContextHolder.getContext().getAuthentication() == null
                    && !jwtService.isTokenExpired(token)) {

                Long userId = jwtService.extractUserId(token);

                // Spring Security's core representation of "this request is authenticated as X".
                // No password needed here — the token itself already proved identity.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                authToken.setDetails(userId); // stash userId for later retrieval in controllers/services

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Invalid/expired/tampered token — leave the request unauthenticated.
            // Downstream authorization rules will reject it with 401/403 as appropriate.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}