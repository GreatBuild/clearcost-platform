package com.greatbuild.clearcost.msvc.projects.models.dtos;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectRole;
import com.greatbuild.clearcost.msvc.projects.models.enums.Specialty;

/**
 * DTO de respuesta para ProjectMember con datos del usuario
 */
public class ProjectMemberResponseDTO {

    private Long memberId;
    private Long userId;
    private String fullName;  // firstName + lastName
    private String email;
    private ProjectRole role;
    private Specialty specialty;

    // Constructors
    public ProjectMemberResponseDTO() {
    }

    public ProjectMemberResponseDTO(Long memberId, Long userId, String fullName, String email, 
                                   ProjectRole role, Specialty specialty) {
        this.memberId = memberId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.specialty = specialty;
    }

    // Getters and Setters
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public Specialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(Specialty specialty) {
        this.specialty = specialty;
    }
}
