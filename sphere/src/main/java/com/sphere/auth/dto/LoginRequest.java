package com.sphere.auth.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Email or username is required")
    @JsonAlias("email")
    private String identifier;

    @NotBlank(message = "Password is required")
    private String password;
}
