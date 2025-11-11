package com.greatbuild.clearcost.msvc.projects.models.entities;

import com.greatbuild.clearcost.msvc.projects.models.enums.ProjectRole;
import com.greatbuild.clearcost.msvc.projects.models.enums.Specialty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Miembro de un proyecto
 */
@Entity
@Table(name = "project_members")
public class ProjectMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private Long userId;  // FK lógica a User en msvc-users

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Specialty specialty;

    // Constructors
    public ProjectMember() {
    }

    public ProjectMember(Long userId, ProjectRole role, Specialty specialty) {
        this.userId = userId;
        this.role = role;
        this.specialty = specialty;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
