package com.app.login.service;

import com.app.login.controller.models.AuthResponse;
import com.app.login.controller.models.AuthenticationRequest;
import com.app.login.controller.models.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse authenticate(AuthenticationRequest request);

}
