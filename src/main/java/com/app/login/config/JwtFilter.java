package com.app.login.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Autowired
    public JwtFilter(UserDetailsService userDetailsService, JwtService jwtService) {
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        System.out.println("JwtFilter: Request URL: " + request.getRequestURI()); // Log URL
        System.out.println("JwtFilter: Authorization Header: " + authHeader); // Log Header

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtFilter: No Bearer token found, skipping filter.");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        System.out.println("JwtFilter: Extracted JWT: " + jwt);

        try {
            userEmail = jwtService.getUserName(jwt);
            System.out.println("JwtFilter: Extracted UserEmail: " + userEmail);
        } catch (Exception e) {
            System.err.println("JwtFilter: Error extracting username from token: " + e.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            System.out.println("JwtFilter: UserDetails loaded for: " + userDetails.getUsername());

            if (jwtService.validateToken(jwt, userDetails)) {
                System.out.println("JwtFilter: Token is valid.");
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No credentials needed here
                        userDetails.getAuthorities()
                );
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("JwtFilter: Authentication set in SecurityContext.");
            } else {
                System.out.println("JwtFilter: Token is invalid.");
            }
        }

        filterChain.doFilter(request, response);
    }
}
