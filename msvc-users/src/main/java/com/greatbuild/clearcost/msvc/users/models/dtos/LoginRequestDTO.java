package com.greatbuild.clearcost.msvc.users.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class LoginRequestDTO {
    @NotEmpty @Email
    private String email;

    @NotEmpty
    private String password;

    // Getters y Setters (necesarios para la deserialización desde JSON)
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
