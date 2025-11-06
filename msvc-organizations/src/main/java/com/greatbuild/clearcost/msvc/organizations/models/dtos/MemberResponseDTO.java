package com.greatbuild.clearcost.msvc.organizations.models.dtos;

/**
 * DTO para la respuesta de un miembro de organización
 * Incluye datos del OrganizationMember + datos del User de msvc-users
 */
public class MemberResponseDTO {

    private Long memberId;        // ID de OrganizationMember
    private Long userId;          // ID del User en msvc-users
    private String role;          // Rol dentro de la organización (CONTRACTOR, MEMBER)
    
    // Datos del usuario de msvc-users
    private String email;
    private String firstName;
    private String lastName;
    
    public MemberResponseDTO() {
    }
    
    public MemberResponseDTO(Long memberId, Long userId, String role, String email, String firstName, String lastName) {
        this.memberId = memberId;
        this.userId = userId;
        this.role = role;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Getters y Setters
    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setLastLastName(String lastName) {
        this.lastName = lastName;
    }
}
