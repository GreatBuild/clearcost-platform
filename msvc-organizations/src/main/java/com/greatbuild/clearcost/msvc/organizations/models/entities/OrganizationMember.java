package com.greatbuild.clearcost.msvc.organizations.models.entities;

import com.greatbuild.clearcost.msvc.organizations.models.enums.OrganizationRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "organization_members")
public class OrganizationMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- ¡CONEXIÓN LÓGICA CON msvc-users! ---
    // Este es el ID del usuario (de msvc-users) que es el miembro
    @Column(nullable = false)
    private Long userId;

    // Este es el rol DENTRO de la organización
    // CONTRACTOR: Dueño de la organización
    // MEMBER: Miembro/empleado de la organización
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private OrganizationRole role;

    // (El @ManyToOne a Organization no es necesario si usas @JoinColumn
    // en la clase Organization, así mantenemos esta entidad más simple)

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public OrganizationRole getRole() { return role; }
    public void setRole(OrganizationRole role) { this.role = role; }
}
