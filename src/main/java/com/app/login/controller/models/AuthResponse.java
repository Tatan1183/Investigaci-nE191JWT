package com.app.login.controller.models;

public class AuthResponse {

    private String token;

    // --- Constructor sin argumentos ---
    public AuthResponse() {
    }

    // --- Constructor con el token ---
    public AuthResponse(String token) {
        this.token = token;
    }

    // --- Getters y Setters ---
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
