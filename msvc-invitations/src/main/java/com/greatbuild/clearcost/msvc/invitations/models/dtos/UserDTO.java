package com.greatbuild.clearcost.msvc.invitations.models.dtos;

import io.micrometer.common.lang.Nullable;

import java.util.Set;

/**
 * DTO para recibir información de usuarios desde msvc-users
 * Debe coincidir con la entidad User de msvc-users
 */
public class UserDTO {

    private Long id;

    private String email;

    @Nullable
    private String password; // Puede ser null para usuarios de Google OAuth2

    private String firstName;
    private String lastName;
    private String provider; // AuthProvider enum: "LOCAL" o "GOOGLE"
    private Set<RoleDTO> roles;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDTO> roles) {
        this.roles = roles;
    }

    // Métodos de utilidad
    public boolean hasRole(String roleName) {
        return roles != null && roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isGoogleUser() {
        return "GOOGLE".equals(provider);
    }

    public boolean isLocalUser() {
        return "LOCAL".equals(provider);
    }
}
