package com.greatbuild.clearcost.msvc.organizations.models.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    private String legalName;
    @NotEmpty
    private String commercialName;
    @NotEmpty
    @Column(unique = true)
    private String ruc;

    @Column(name = "created_at")
    private LocalDate createdAt;

    // --- ¡CONEXIÓN LÓGICA CON msvc-users! ---
    // Este es el ID del usuario (de msvc-users) que es el "dueño"
    @Column(nullable = false)
    private Long ownerId;

    // --- ¡RELACIÓN CON LOS MIEMBROS! ---
    // Esta es la relación con los miembros DE ESTA organización
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "organization_id") // Columna en la tabla 'organization_members'
    private List<OrganizationMember> members;

    // Constructor
    public Organization() {
        this.members = new ArrayList<>();
        this.createdAt = LocalDate.now();
    }

    // --- Métodos de Dominio (¡Modelo Rico!) ---
    // En lugar de exponer 'setMembers', controlamos cómo se añaden

    public void addMember(OrganizationMember member) {
        this.members.add(member);
    }

    public void removeMember(OrganizationMember member) {
        this.members.remove(member);
    }

    // --- Getters y Setters ---
    // (Omitidos por brevedad, pero debes añadirlos)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    // ... etc. para todos los campos
    public String getLegalName() { return legalName; }
    public void setLegalName(String legalName) { this.legalName = legalName; }
    public String getCommercialName() { return commercialName; }
    public void setCommercialName(String commercialName) { this.commercialName = commercialName; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public List<OrganizationMember> getMembers() { return members; }
    public void setMembers(List<OrganizationMember> members) { this.members = members; }
}