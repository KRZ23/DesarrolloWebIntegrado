package com.devintegrado.impuestoscal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

public final class AuthDtos {
    private AuthDtos() {}
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @NotBlank
        @Size(min = 10, max = 10)
        private String rut10;

        @NotBlank
        @Size(min = 4, max = 100)
        private String claveSol;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private String tokenType;
        private String rut10;
        private Set<String> roles;
    }
}


