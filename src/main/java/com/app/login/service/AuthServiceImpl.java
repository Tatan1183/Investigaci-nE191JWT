package com.app.login.service;

import com.app.login.config.JwtService;
import com.app.login.controller.models.AuthResponse;
import com.app.login.controller.models.AuthenticationRequest;
import com.app.login.controller.models.RegisterRequest;
import com.app.login.entity.Role;
import com.app.login.entity.User;
import com.app.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor para inyección de dependencias (sin cambios aquí)
    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // --- SOLUCIÓN: Crear User usando el constructor ---
        User user = new User(
                request.getNombre(),
                request.getApellido(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()), // Codificar contraseña aquí
                Role.USER // Asignar rol
        );

        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);

        // --- SOLUCIÓN: Crear AuthResponse usando el constructor ---
        return new AuthResponse(jwtToken);
    }

    @Override
    public AuthResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + request.getEmail()));
        var jwtToken = jwtService.generateToken(user);

        // --- SOLUCIÓN: Crear AuthResponse usando el constructor ---
        return new AuthResponse(jwtToken);
    }
}
