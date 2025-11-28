package com.devintegrado.impuestoscal.service;

import com.devintegrado.impuestoscal.dto.AuthDtos;

public interface AuthService {
    AuthDtos.LoginResponse login(AuthDtos.LoginRequest request);
}



